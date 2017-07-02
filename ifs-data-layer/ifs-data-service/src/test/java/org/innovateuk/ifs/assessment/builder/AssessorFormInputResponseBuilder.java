package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.form.domain.FormInput;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse}
 */
public class AssessorFormInputResponseBuilder extends BaseBuilder<AssessorFormInputResponse, AssessorFormInputResponseBuilder> {

    private AssessorFormInputResponseBuilder(List<BiConsumer<Integer, AssessorFormInputResponse>> newActions) {
        super(newActions);
    }

    public static AssessorFormInputResponseBuilder newAssessorFormInputResponse() {
        return new AssessorFormInputResponseBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessorFormInputResponseBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorFormInputResponse>> actions) {
        return new AssessorFormInputResponseBuilder(actions);
    }

    @Override
    protected AssessorFormInputResponse createInitial() {
        return new AssessorFormInputResponse();
    }

    public AssessorFormInputResponseBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessorFormInputResponseBuilder withAssessment(Assessment... assessments) {
        return withArray((assessment, assessorFormInputResponse) -> setField("assessment", assessment, assessorFormInputResponse), assessments);
    }

    public AssessorFormInputResponseBuilder withFormInput(FormInput... formInputs) {
        return withArray((formInput, assessorFormInputResponse) -> setField("formInput", formInput, assessorFormInputResponse), formInputs);
    }

    public AssessorFormInputResponseBuilder withValue(String... values) {
        return withArray(BaseBuilderAmendFunctions::setValue, values);
    }

    public AssessorFormInputResponseBuilder withUpdatedDate(ZonedDateTime... updatedDates) {
        return withArray((updatedDate, assessorFormInputResponse) -> setField("updatedDate", updatedDate, assessorFormInputResponse), updatedDates);
    }
}
