package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScoringStrategyFactory {

    private final Map<Question.ScoringStrategyType, ScoringStrategy> strategies;

    public ScoringStrategyFactory() {
        this.strategies = Map.of(
                Question.ScoringStrategyType.ALL_OR_NOTHING, new AllOrNothingScoringStrategy(),
                Question.ScoringStrategyType.PARTIAL, new PartialScoringStrategy(),
                Question.ScoringStrategyType.NONE, new NoneScoringStrategy(),
                Question.ScoringStrategyType.MANUAL, new ManualScoringStrategy()
        );
    }

    public ScoringStrategy get(Question.ScoringStrategyType type) {
        return strategies.get(type);
    }
}
