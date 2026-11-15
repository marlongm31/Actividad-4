package com.barberturn.adapter.in.web.dto;

import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
}
