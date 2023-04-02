package com.picpay.quickstart.misc.tracing

import br.com.guiabolso.events.context.EventThreadContextManager
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.builder.TracerBuilder

object Tracer : Tracer by TracerBuilder()
    .withDatadogAPM()
    .withSlf4J()
    .withContextManager(EventThreadContextManager)
    .build()
