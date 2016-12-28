package org.innovateuk.ifs.project.finance.workflow.financechecks.guards;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.finance.domain.FinanceCheck;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * This asserts that all Finance Check figures have been entered for the given Partner Organisation prior to allowing
 * them to be approved.
 */
@Component
public class AllFinanceCheckFiguresEnteredGuard implements Guard<FinanceCheckState, FinanceCheckOutcomes> {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Override
    public boolean evaluate(StateContext<FinanceCheckState, FinanceCheckOutcomes> context) {

        PartnerOrganisation partnerOrganisation = (PartnerOrganisation) context.getMessageHeader("target");

        return validateIsReadyForApprovalWithAllFiguresEntered(partnerOrganisation);
    }

    private boolean validateIsReadyForApprovalWithAllFiguresEntered(PartnerOrganisation partnerOrganisation) {

        FinanceCheck financeCheck = financeCheckRepository.findByProjectIdAndOrganisationId(
                partnerOrganisation.getProject().getId(),
                partnerOrganisation.getOrganisation().getId());

        return financeCheck.getCostGroup().getCosts().stream().allMatch(c -> c.getValue() != null);
    }
}
