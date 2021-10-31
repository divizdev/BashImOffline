package dev.diviz.bashoffline

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RTETest {
    @Test
    fun simpleTest() {
        val latch = CountDownLatch(1)
        var result = false
        val job = RTE(mainExecutor = Executors.newSingleThreadExecutor(), task = {
            true
        }).then({
            latch.countDown()
        }) {
            result = it
            latch.countDown()
        }
        latch.await()
        assert(result)
    }

    @Test
    fun cancelJob() {
        val latch = CountDownLatch(1)
        var result = false
        val job =  runJob(mainExecutor = Executors.newSingleThreadExecutor()) {
            while (!it.isCancel) {
            }
            result = true
            latch.countDown()
            false
        }.then({
            throw(it)
        }) {
            result = it
        }
        Thread.sleep(1000)
        job.cancel()
        latch.await(1000, TimeUnit.MILLISECONDS)
        assert(result)
    }
}