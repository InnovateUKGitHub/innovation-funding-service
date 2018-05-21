package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class InterviewAssignmentMessageOutcomeBuilder extends BaseBuilder<InterviewAssignmentMessageOutcome, InterviewAssignmentMessageOutcomeBuilder> {

    private InterviewAssignmentMessageOutcomeBuilder(List<BiConsumer<Integer, InterviewAssignmentMessageOutcome>> multiActions) {
        super(multiActions);
    }

    public static InterviewAssignmentMessageOutcomeBuilder newInterviewAssignmentMessageOutcome() {
        return new InterviewAssignmentMessageOutcomeBuilder(emptyList());
    }

    @Override
    protected InterviewAssignmentMessageOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewAssignmentMessageOutcome>> actions) {
        return new InterviewAssignmentMessageOutcomeBuilder(actions);
    }

    @Override
    protected InterviewAssignmentMessageOutcome createInitial() {
        return new InterviewAssignmentMessageOutcome();
    }

    public InterviewAssignmentMessageOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public InterviewAssignmentMessageOutcomeBuilder withSubject(String... subjects) {
        return withArray((subject, assessmentInterviewPanelMessageOutcome) -> assessmentInterviewPanelMessageOutcome.setMessage(subject), subjects);
    }

    public InterviewAssignmentMessageOutcomeBuilder withMessage(String... messages) {
        return withArray((message, assessmentInterviewPanelMessageOutcome) -> assessmentInterviewPanelMessageOutcome.setMessage(message), messages);
    }

    public InterviewAssignmentMessageOutcomeBuilder withFeedback(FileEntry... feedbacks) {
        return withArray((feedback, assessmentInterviewPanelMessageOutcome) -> assessmentInterviewPanelMessageOutcome.setFeedback(feedback), feedbacks);
    }

    public InterviewAssignmentMessageOutcomeBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArray((modifiedOn, user) -> setField("modifiedOn", modifiedOn, user), modifiedOns);
    }

}