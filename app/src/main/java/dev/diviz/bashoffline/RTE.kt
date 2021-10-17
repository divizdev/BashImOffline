package dev.diviz.bashoffline

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory


class RTE<R>(
    private var task: ((jobContext: JobContext) -> R)?,
    private val workerExecutor: ExecutorService = defaultWorkerExecutor,
    private val mainExecutor: Executor = defaultMainExecutor,
) {

    companion object {
        private var currNumber = 1
        private const val numberWorkers = 2 // * Runtime.getRuntime().availableProcessors()

        val defaultWorkerExecutor: ExecutorService = Executors.newFixedThreadPool(numberWorkers, getThreadFactory("RTE"))

        val defaultMainExecutor: Executor = object : Executor {
            private val handler = Handler(Looper.getMainLooper())

            override fun execute(command: Runnable?) {
                if (command == null) return
                handler.post(command)
            }
        }

        private fun getThreadFactory(name: String, priority: Int = Thread.NORM_PRIORITY): ThreadFactory {
            return ThreadFactory { r ->
                val t = Thread(r, name + currNumber.toString())
                t.priority = priority
                currNumber++
                t
            }
        }
    }

    fun then(onError: (error: Throwable) -> Unit, onResult: (value: R) -> Unit): Job {
        val job = InternalJob()
        val res = workerExecutor.submit {
            try {
                val result = task!!(job.context)
                task = null //TODO: Захват контектса

                Log.d("RTE", "${!job.context.isCancel} && ${!Thread.currentThread().isInterrupted} && ${job.future?.isCancelled}")

                if (!job.context.isCancel && !Thread.currentThread().isInterrupted) {
                    mainExecutor.execute {
                        onResult(result)
                    }
                }
            } catch (ex: Exception) {
                task = null
                mainExecutor.execute {
                    onError(ex)
                }
            }
        }
        job.future = res
        return job
    }

}


class JobContext {

    @Volatile
    var isCancel: Boolean = false
        private set

    fun cancel() {
        synchronized(this) {
            isCancel = true
        }
    }

}

private class InternalJob(public override val context: JobContext = JobContext()): Job(context) {
    @Volatile
    public override var future: Future<*>? = null

}

open class Job(protected open val context: JobContext = JobContext()) {
    @Volatile
    protected open var future: Future<*>? = null

    open fun cancel() {
        synchronized(this) {
            context.cancel()
            future?.cancel(false)
        }
    }
}

fun <T> runJob(task: (jobContext: JobContext) -> T): RTE<T> = RTE(task)

