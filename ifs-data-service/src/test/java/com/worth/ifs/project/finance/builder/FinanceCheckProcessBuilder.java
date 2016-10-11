package com.worth.ifs.project.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.finance.domain.FinanceCheckProcess;
import com.worth.ifs.user.domain.User;

import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FinanceCheckProcessBuilder extends BaseBuilder<FinanceCheckProcess, FinanceCheckProcessBuilder> {

    private FinanceCheckProcessBuilder(List<BiConsumer<Integer, FinanceCheckProcess>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckProcessBuilder newFinanceCheckProcess() {
        return new FinanceCheckProcessBuilder(emptyList());
    }

    @Override
    protected FinanceCheckProcessBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckProcess>> actions) {
        return new FinanceCheckProcessBuilder(actions);
    }

    @Override
    protected FinanceCheckProcess createInitial() {
        return newInstance(FinanceCheckProcess.class);
    }


    public FinanceCheckProcessBuilder withParticipant(ProjectUser... participant) {
        return withArray((p, financeCheck) -> financeCheck.setParticipant(p), participant);
    }

    public FinanceCheckProcessBuilder withInternalParticipant(User... internalParticipant) {
        return withArray((p, financeCheck) -> financeCheck.setInternalParticipant(p), internalParticipant);
    }

    public FinanceCheckProcessBuilder withModifiedDate(Calendar... modifiedDate) {
        return withArray((d, financeCheck) -> financeCheck.setLastModified(d), modifiedDate);
    }
}
