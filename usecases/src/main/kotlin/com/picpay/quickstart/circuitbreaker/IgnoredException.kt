package com.picpay.quickstart.circuitbreaker

import br.com.guiabolso.events.exception.EventException

class IgnoredException : EventException(
    code = "IGNORED_EXCEPTION",
    parameters = emptyMap(),
)