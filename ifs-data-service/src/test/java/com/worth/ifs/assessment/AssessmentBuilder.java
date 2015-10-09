package com.worth.ifs.assessment;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 09/10/15.
 */
public class AssessmentBuilder extends BaseBuilder<Assessment> {

    private AssessmentBuilder(List<Consumer<Assessment>> newActions, List<BiConsumer<Integer, Assessment>> multiActions) {
        super(newActions, multiActions);
    }

    private AssessmentBuilder() {
    }

    public static AssessmentBuilder newAssessment() {
        return new AssessmentBuilder();
    }

    @Override
    protected BaseBuilder<Assessment> createNewBuilderWithActions(List<Consumer<Assessment>> actions, List<BiConsumer<Integer, Assessment>> multiActions) {
        return new AssessmentBuilder(actions, multiActions);
    }

    @Override
    protected Assessment createInitial() {
        return new Assessment();
    }
}
