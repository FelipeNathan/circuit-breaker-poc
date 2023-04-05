package com.picpay.quickstart

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProviderService {

    fun disable() = log.info(
        "\n=== CALL EVENT TO DISABLE PROVIDER === "
    )

    fun enable() =
        log.info("=== CALL EVENT TO ENABLE PROVIDER JUST TO TEST, DO NOT CLOSE CIRCUIT BREAKER YET ===")

    companion object {
        val log = LoggerFactory.getLogger(ProviderService::class.java)
    }
}