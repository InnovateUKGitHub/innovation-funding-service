package com.worth.ifs.assessment;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;

/**
 * Created by dwatson on 09/10/15.
 */
public class AssessmentBuilder extends BaseBuilder<Assessment, AssessmentBuilder> {

    private AssessmentBuilder(List<BiConsumer<Integer, Assessment>> multiActions) {
        super(multiActions);
    }

    private AssessmentBuilder() {
    }

    public static AssessmentBuilder newAssessment() {
        return new AssessmentBuilder();
    }

    @Override
    protected AssessmentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Assessment>> actions) {
        return new AssessmentBuilder(actions);
    }

    @Override
    protected Assessment createInitial() {
        return new Assessment();
    }

    public AssessmentBuilder withApplication(Application... applications) {
        return with((application, assessment) -> setField("application", application, assessment), applications);
    }
}
