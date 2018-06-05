package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class InterviewBuilder extends BaseBuilder<Interview, InterviewBuilder> {

    private InterviewBuilder(List<BiConsumer<Integer, Interview>> multiActions) {
        super(multiActions);
    }

    public static InterviewBuilder newInterview() {
        return new InterviewBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InterviewBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Interview>> actions) {
        return new InterviewBuilder(actions);
    }

    @Override
    protected Interview createInitial() {
        return new Interview();
    }

    public InterviewBuilder withId(Long... ids) {
        return withArray((id, interview) -> setField("id", id, interview), ids);
    }

    public InterviewBuilder withTarget(Application... applications) {
        return withArray((application, interview) -> interview.setTarget(application), applications);
    }

    public InterviewBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, interview) -> interview.setParticipant(participant), participants);
    }

    public InterviewBuilder withState(InterviewState... states) {
        return withArray((state, interview) -> interview.setProcessState(state), states);
    }
}