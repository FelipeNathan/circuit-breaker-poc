package com.picpay.quickstart.config.async

import java.lang.Thread.NORM_PRIORITY
import java.lang.Thread.currentThread
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class CustomThreadFactory(private val prefix: String) : ThreadFactory {

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

    private val group = currentThread().threadGroup
    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r, "$prefix[${poolNumber.get()}]-pool-${threadNumber.getAndIncrement()}", 0)
        if (t.isDaemon) t.isDaemon = false
        if (t.priority != NORM_PRIORITY) t.priority = NORM_PRIORITY
        return t
    }
}
