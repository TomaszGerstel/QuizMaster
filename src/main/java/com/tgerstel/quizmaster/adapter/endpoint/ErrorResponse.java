package com.tgerstel.quizmaster.adapter.endpoint;

public record ErrorResponse(int statusCode, String reason) {
}
