package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessorFormInputResponseResource}
 */
public class AssessorFormInputResponseResourceBuilder extends BaseBuilder<AssessorFormInputResponseResource, AssessorFormInputResponseResourceBuilder> {

    private AssessorFormInputResponseResourceBuilder(List<BiConsumer<Integer, AssessorFormInputResponseResource>> newActions) {
        super(newActions);
    }

    public static AssessorFormInputResponseResourceBuilder newAssessorFormInputResponseResource() {
        return new AssessorFormInputResponseResourceBuilder(emptyList()).with(BaseBuilderAmendFunctions.uniqueIds());
    }

    @Override
    protected AssessorFormInputResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorFormInputResponseResource>> actions) {
        return new AssessorFormInputResponseResourceBuilder(actions);
    }

    @Override
    protected AssessorFormInputResponseResource createInitial() {
        return new AssessorFormInputResponseResource();
    }

    public AssessorFormInputResponseResourceBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessorFormInputResponseResourceBuilder withAssessment(Long... assessments) {
        return withArray((assessment, assessorFormInputResponse) -> BaseBuilderAmendFunctions.setField("assessment", assessment, assessorFormInputResponse), assessments);
    }

    public AssessorFormInputResponseResourceBuilder withQuestion(Long... questions) {
        return withArray((question, assessorFormInputResponse) -> BaseBuilderAmendFunctions.setField("question", question, assessorFormInputResponse), questions);
    }

    public AssessorFormInputResponseResourceBuilder withFormInput(Long... formInputs) {
        return withArray((formInput, assessorFormInputResponse) -> BaseBuilderAmendFunctions.setField("formInput", formInput, assessorFormInputResponse), formInputs);
    }

    public AssessorFormInputResponseResourceBuilder withValue(String... values) {
        return withArray(BaseBuilderAmendFunctions::setValue, values);
    }

    public AssessorFormInputResponseResourceBuilder withFormInputMaxWordCount(Integer... formInputMaxWordCounts) {
        return withArray((formInputMaxWordCount, assessorFormInputResponse) -> BaseBuilderAmendFunctions.setField("formInputMaxWordCount", formInputMaxWordCount, assessorFormInputResponse), formInputMaxWordCounts);
    }

    public AssessorFormInputResponseResourceBuilder withUpdatedDate(ZonedDateTime... updatedDates) {
        return withArray((updatedDate, assessorFormInputResponse) -> BaseBuilderAmendFunctions.setField("updatedDate", updatedDate, assessorFormInputResponse), updatedDates);
    }
}
