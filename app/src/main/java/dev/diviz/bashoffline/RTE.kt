package dev.diviz.bashoffline

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class RTE<R>(val task: (jobContext: JobContext) -> R, private val defaultTaskExecutor: ExecutorService = singleQueueExecutor("RTE", Thread.NORM_PRIORITY)) {

    fun then(onError: (error: Throwable) -> Unit, onResult: (value: R) -> Unit): Job {
        val job = Job()
        defaultTaskExecutor.execute {
            try {
                val result = task(job.context)
                Handler(Looper.getMainLooper()).post {
                    onResult(result)
                }
            } catch (ex: Exception) {
                Handler(Looper.getMainLooper()).post {
                    onError(ex)
                }
            }
        }
        return Job()
    }

}


class JobContext{

}

class Job(val context: JobContext = JobContext()){

}


private fun singleQueueExecutor(name: String, priority: Int): ThreadPoolExecutor {
    return ThreadPoolExecutor(
        1, 1,
        0L, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(1),
        getThreadFactory(name, priority),
        ThreadPoolExecutor.DiscardOldestPolicy()
    )
}

private fun getThreadFactory(name: String, priority: Int): ThreadFactory {
    return ThreadFactory { r ->
        val t = Thread(r, name)
        t.priority = priority
        t
    }
}

fun <T> runJob(task: (jobContext: JobContext) -> T ): RTE<T> {

        return RTE(task)

}

