package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
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

    public AssessorFormInputResponseResourceBuilder withFormInput(Long... formInputs) {
        return withArray((formInput, assessorFormInputResponse) -> setField("formInput", formInput, assessorFormInputResponse), formInputs);
    }

    public AssessorFormInputResponseResourceBuilder withNumericValue(Integer... numericValues) {
        return withArray((numericValue, assessorFormInputResponse) -> setField("numericValue", numericValue, assessorFormInputResponse), numericValues);
    }

    public AssessorFormInputResponseResourceBuilder withTextValue(String... textValues) {
        return withArray((textValue, assessorFormInputResponse) -> setField("textValue", textValue, assessorFormInputResponse), textValues);
    }

    public AssessorFormInputResponseResourceBuilder withUpdatedDate(LocalDateTime... updatedDates) {
        return withArray((updatedDate, assessorFormInputResponse) -> setField("updatedDate", updatedDate, assessorFormInputResponse), updatedDates);
    }
}