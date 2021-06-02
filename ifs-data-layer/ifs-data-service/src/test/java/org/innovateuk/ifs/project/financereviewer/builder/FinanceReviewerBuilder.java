package org.innovateuk.ifs.project.financereviewer.builder;

import org.innovateuk.ifs.project.core.builder.ProjectParticipantBuilder;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.financereviewer.domain.FinanceReviewer;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class FinanceReviewerBuilder extends ProjectParticipantBuilder<FinanceReviewer, FinanceReviewerBuilder> {

    private FinanceReviewerBuilder(List<BiConsumer<Integer, FinanceReviewer>> multiActions) {
        super(multiActions, EnumSet.of(ProjectParticipantRole.FINANCE_REVIEWER));
    }

    public static FinanceReviewerBuilder newFinanceReviewer() {
        return new FinanceReviewerBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FinanceReviewerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceReviewer>> actions) {
        return new FinanceReviewerBuilder(actions);
    }

    @Override
    protected FinanceReviewer createInitial() {
        return new FinanceReviewer();
    }

    @Override
    public void postProcess(int index, FinanceReviewer financeReviewer) {
        Optional.ofNullable(financeReviewer.getProcess())
                .ifPresent(p -> p.setFinanceReviewer(financeReviewer));
    }
}