package com.worth.ifs.project.finance.workflow.financechecks.guards;

import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.finance.resource.FinanceCheckOutcomes;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * This asserts that all Finance Check figures have been entered for the given Partner Organisation prior to allowing
 * them to be approved.
 */
@Component
public class AllFinanceCheckFiguresEnteredGuard implements Guard<FinanceCheckState, FinanceCheckOutcomes> {

    @Override
    public boolean evaluate(StateContext<FinanceCheckState, FinanceCheckOutcomes> context) {

        PartnerOrganisation partnerOrganisation = (PartnerOrganisation) context.getMessageHeader("target");

        return validateIsReadyForApproval(partnerOrganisation);
    }

    private boolean validateIsReadyForApproval(PartnerOrganisation partnerOrganisation) {
        // TODO DW - implement checking for all finance figures entered before approval possible
        return true;
    }
}
