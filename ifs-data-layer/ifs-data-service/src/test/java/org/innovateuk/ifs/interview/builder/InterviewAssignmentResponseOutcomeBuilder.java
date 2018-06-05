package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class InterviewAssignmentResponseOutcomeBuilder extends BaseBuilder<InterviewAssignmentResponseOutcome, InterviewAssignmentResponseOutcomeBuilder> {

    private InterviewAssignmentResponseOutcomeBuilder(List<BiConsumer<Integer, InterviewAssignmentResponseOutcome>> multiActions) {
        super(multiActions);
    }

    public static InterviewAssignmentResponseOutcomeBuilder newInterviewAssignmentResponseOutcome() {
        return new InterviewAssignmentResponseOutcomeBuilder(emptyList());
    }

    @Override
    protected InterviewAssignmentResponseOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentResponseOutcome>> actions) {
        return new InterviewAssignmentResponseOutcomeBuilder(actions);
    }

    @Override
    protected InterviewAssignmentResponseOutcome createInitial() {
        return new InterviewAssignmentResponseOutcome();
    }

    public InterviewAssignmentResponseOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public InterviewAssignmentResponseOutcomeBuilder withResponse(String... responses) {
        return withArray((response, assessmentInterviewPanelResponseOutcome) -> assessmentInterviewPanelResponseOutcome.setResponse(response), responses);
    }

    public InterviewAssignmentResponseOutcomeBuilder withFileResponse(FileEntry... responses) {
        return withArray((fileResponse, assessmentInterviewPanelResponseOutcome) -> assessmentInterviewPanelResponseOutcome.setFileResponse(fileResponse), responses);
    }
}