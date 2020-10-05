package org.innovateuk.ifs.cofunder.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CofunderAssignmentBuilder extends BaseBuilder<CofunderAssignment, CofunderAssignmentBuilder> {

    private CofunderAssignmentBuilder(List<BiConsumer<Integer, CofunderAssignment>> multiActions) {
        super(multiActions);
    }

    public static CofunderAssignmentBuilder newCofunderAssignment() {
        return new CofunderAssignmentBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CofunderAssignmentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CofunderAssignment>> actions) {
        return new CofunderAssignmentBuilder(actions);
    }

    @Override
    protected CofunderAssignment createInitial() {
        return new CofunderAssignment();
    }

    public CofunderAssignmentBuilder withId(Long... ids) {
        return withArray((id, CofunderAssignment) -> setField("id", id, CofunderAssignment), ids);
    }


    public CofunderAssignmentBuilder withStartDate(LocalDate... startDates) {
        return withArray((startDate, object) -> setField("startDate", startDate, object), startDates);
    }

    public CofunderAssignmentBuilder withEndDate(LocalDate... endDates) {
        return withArray((endDate, object) -> setField("endDate", endDate, object), endDates);
    }

    public CofunderAssignmentBuilder withParticipant(User... processRoles) {
        return withArray((processRole, object) -> setField("participant", processRole, object), processRoles);
    }

    public CofunderAssignmentBuilder withApplication(Application... application) {
        return withArray((app, object) -> setField("target", app, object), application);
    }

    public CofunderAssignmentBuilder withProcessState(CofunderState... activityState) {
        return withArray((state, object) -> object.setProcessState(state), activityState);
    }

    public CofunderAssignmentBuilder withCofunderOutcome(CofunderOutcome... fundingDecision) {
        return withArraySetFieldByReflection("cofunderOutcome", fundingDecision);
    }
}
