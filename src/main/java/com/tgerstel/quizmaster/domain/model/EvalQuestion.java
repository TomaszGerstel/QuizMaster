package com.tgerstel.quizmaster.domain.model;

import java.util.List;

public record EvalQuestion (String id, String explanation, List<EvalAnswer> answers, String author) {

}
