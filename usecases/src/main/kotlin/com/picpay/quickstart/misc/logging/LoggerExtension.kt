package com.picpay.quickstart.misc.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

typealias MDCContext = Map<String, String>

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun <T> withContext(context: MDCContext, func: () -> T): T {
    try {
        context.forEach { (key, value) -> MDC.put(key, value) }
        return func()
    } finally {
        context.forEach { (key, _) -> MDC.remove(key) }
    }
}

fun Logger.infoWithContext(message: String, context: MDCContext = HashMap()) {
    withContext(context) {
        this.info(message)
    }
}

fun Logger.errorWithContext(message: String, e: Throwable, context: MDCContext = HashMap()) {
    withContext(context) {
        this.error(message, e)
    }
}

fun Logger.warnWithContext(message: String, context: MDCContext = HashMap()) {
    withContext(context) {
        this.warn(message)
    }
}

fun Logger.debugWithContext(message: String, context: MDCContext = HashMap()) {
    withContext(context) {
        this.debug(message)
    }
}

fun Logger.traceWithContext(message: String, context: MDCContext = HashMap()) {
    withContext(context) {
        this.trace(message)
    }
}
