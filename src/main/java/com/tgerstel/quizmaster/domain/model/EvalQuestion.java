package com.tgerstel.quizmaster.domain.model;

import java.util.List;

public record EvalQuestion (String id, List<EvalAnswer> answers) {

}
