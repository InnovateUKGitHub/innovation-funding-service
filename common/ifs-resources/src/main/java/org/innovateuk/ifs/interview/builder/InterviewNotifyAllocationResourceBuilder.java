package org.innovateuk.ifs.interview.builder;
import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.interview.resource.InterviewNotifyAllocationResource;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Resource builder for InterviewNotifyAllocationResources
 */
public class InterviewNotifyAllocationResourceBuilder
    extends BaseBuilder<InterviewNotifyAllocationResource, InterviewNotifyAllocationResourceBuilder> {

    protected InterviewNotifyAllocationResourceBuilder() {
        super();
    }

    protected InterviewNotifyAllocationResourceBuilder(List<BiConsumer<Integer, InterviewNotifyAllocationResource>> newActions) {
        super(newActions);
    }

    public static InterviewNotifyAllocationResourceBuilder newInterviewNotifyAllocationResource() {
        return new InterviewNotifyAllocationResourceBuilder();
    }

    @Override
    protected InterviewNotifyAllocationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InterviewNotifyAllocationResource>> actions) {
        return new InterviewNotifyAllocationResourceBuilder(actions);
    }

    @Override
    protected InterviewNotifyAllocationResource createInitial() {
        return new InterviewNotifyAllocationResource();
    }

    public InterviewNotifyAllocationResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }

    public InterviewNotifyAllocationResourceBuilder withAssessorId(Long... assessorId) {
        return withArraySetFieldByReflection("assessorId", assessorId);
    }

    public InterviewNotifyAllocationResourceBuilder withSubject(String... subject) {
        return withArraySetFieldByReflection("subject", subject);
    }

    public InterviewNotifyAllocationResourceBuilder withContent(String... content) {
        return withArraySetFieldByReflection("content", content);
    }

    public InterviewNotifyAllocationResourceBuilder withApplicationIds(List<Long>... applicationIdsList) {
        return withArraySetFieldByReflection("applicationIds", applicationIdsList);
    }
}