package com.picpay.quickstart

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProviderService {

    fun disable() = log.info(
        "\n=== CALL EVENT TO DISABLE PROVIDER === "
    )

    fun enable() = log.info(
        "\n=== CALL EVENT TO ENABLE PROVIDER ==="
    )

    companion object {
        val log = LoggerFactory.getLogger(ProviderService::class.java)
    }
}
