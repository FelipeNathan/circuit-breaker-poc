package com.picpay.quickstart

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProviderService {

    fun disable(origin: String) = log.info(
        "\n=== CALL EVENT TO DISABLE PROVIDER FOR ORIGIN $origin=== "
    )

    fun enable(origin: String) = log.info(
        "\n=== CALL EVENT TO ENABLE PROVIDER FOR ORIGIN $origin==="
    )

    companion object {
        val log = LoggerFactory.getLogger(ProviderService::class.java)
    }
}
