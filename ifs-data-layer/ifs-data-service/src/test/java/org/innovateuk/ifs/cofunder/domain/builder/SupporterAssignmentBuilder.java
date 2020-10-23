package org.innovateuk.ifs.supporter.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.domain.SupporterOutcome;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SupporterAssignmentBuilder extends BaseBuilder<SupporterAssignment, SupporterAssignmentBuilder> {

    private SupporterAssignmentBuilder(List<BiConsumer<Integer, SupporterAssignment>> multiActions) {
        super(multiActions);
    }

    public static SupporterAssignmentBuilder newSupporterAssignment() {
        return new SupporterAssignmentBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SupporterAssignmentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SupporterAssignment>> actions) {
        return new SupporterAssignmentBuilder(actions);
    }

    @Override
    protected SupporterAssignment createInitial() {
        return new SupporterAssignment();
    }

    public SupporterAssignmentBuilder withId(Long... ids) {
        return withArray((id, SupporterAssignment) -> setField("id", id, SupporterAssignment), ids);
    }


    public SupporterAssignmentBuilder withStartDate(LocalDate... startDates) {
        return withArray((startDate, object) -> setField("startDate", startDate, object), startDates);
    }

    public SupporterAssignmentBuilder withEndDate(LocalDate... endDates) {
        return withArray((endDate, object) -> setField("endDate", endDate, object), endDates);
    }

    public SupporterAssignmentBuilder withParticipant(User... processRoles) {
        return withArray((processRole, object) -> setField("participant", processRole, object), processRoles);
    }

    public SupporterAssignmentBuilder withApplication(Application... application) {
        return withArray((app, object) -> setField("target", app, object), application);
    }

    public SupporterAssignmentBuilder withProcessState(SupporterState... activityState) {
        return withArray((state, object) -> object.setProcessState(state), activityState);
    }

    public SupporterAssignmentBuilder withSupporterOutcome(SupporterOutcome... fundingDecision) {
        return withArraySetFieldByReflection("supporterOutcome", fundingDecision);
    }
}
