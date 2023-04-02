package com.picpay.quickstart

import com.google.gson.GsonBuilder
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.impl.client.HttpClientBuilder

object HttpHelper {

    val gson = GsonBuilder().create()!!

    private val target = HttpHost("localhost", 59999, "http")

    fun execute(request: HttpRequest): HttpResponse {
        val httpClient = HttpClientBuilder.create().build()!!
        return httpClient.execute(target, request)
    }
}
