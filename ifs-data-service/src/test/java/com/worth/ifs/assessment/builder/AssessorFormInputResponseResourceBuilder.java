package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessorFormInputResponseResource}
 */
public class AssessorFormInputResponseResourceBuilder extends BaseBuilder<AssessorFormInputResponseResource, AssessorFormInputResponseResourceBuilder> {

    private AssessorFormInputResponseResourceBuilder(List<BiConsumer<Integer, AssessorFormInputResponseResource>> newActions) {
        super(newActions);
    }

    public static AssessorFormInputResponseResourceBuilder newAssessorFormInputResponseResource() {
        return new AssessorFormInputResponseResourceBuilder(emptyList()).with(uniqueIds());
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
        return withArray(BuilderAmendFunctions::setId, ids);
    }

    public AssessorFormInputResponseResourceBuilder withAssessment(Long... assessments) {
        return withArray((assessment, assessorFormInputResponse) -> setField("assessment", assessment, assessorFormInputResponse), assessments);
    }

    public AssessorFormInputResponseResourceBuilder withQuestion(Long... questions) {
        return withArray((question, assessorFormInputResponse) -> setField("question", question, assessorFormInputResponse), questions);
    }

    public AssessorFormInputResponseResourceBuilder withFormInput(Long... formInputs) {
        return withArray((formInput, assessorFormInputResponse) -> setField("formInput", formInput, assessorFormInputResponse), formInputs);
    }

    public AssessorFormInputResponseResourceBuilder withValue(String... values) {
        return withArray(BuilderAmendFunctions::setValue, values);
    }

    public AssessorFormInputResponseResourceBuilder withFormInputMaxWordCount(Integer... formInputMaxWordCounts) {
        return withArray((formInputMaxWordCount, assessorFormInputResponse) -> setField("formInputMaxWordCount", formInputMaxWordCount, assessorFormInputResponse), formInputMaxWordCounts);
    }

    public AssessorFormInputResponseResourceBuilder withUpdatedDate(LocalDateTime... updatedDates) {
        return withArray((updatedDate, assessorFormInputResponse) -> setField("updatedDate", updatedDate, assessorFormInputResponse), updatedDates);
    }
}