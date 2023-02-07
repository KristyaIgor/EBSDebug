package com.ebs.integrator.ebsdebug.enums

import okhttp3.logging.HttpLoggingInterceptor

enum class EbsLevel(val value: HttpLoggingInterceptor.Level) {
    Body(HttpLoggingInterceptor.Level.BODY),
    Headers(HttpLoggingInterceptor.Level.HEADERS),
    Basic(HttpLoggingInterceptor.Level.BASIC),
    None(HttpLoggingInterceptor.Level.NONE)
}