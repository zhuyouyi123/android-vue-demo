package com.seek.config.annotation;

public enum AppRequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;
    private AppRequestMethod() {
    }
}
