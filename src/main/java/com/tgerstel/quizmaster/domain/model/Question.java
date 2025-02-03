package com.tgerstel.quizmaster.domain.model;

import java.util.List;

public record Question(String id, String question, List<Answer> answers) {
}