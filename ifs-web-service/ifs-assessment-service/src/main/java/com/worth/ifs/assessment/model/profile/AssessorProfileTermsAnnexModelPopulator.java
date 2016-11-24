package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.controller.profile.AssessorProfileTermsController.ContractAnnexParameter;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileTermsAnnexViewModel;
import com.worth.ifs.contract.service.ContractService;
import com.worth.ifs.user.resource.ContractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Terms Annex view.
 */
@Component
public class AssessorProfileTermsAnnexModelPopulator {

    @Autowired
    private ContractService contractService;

    public AssessorProfileTermsAnnexViewModel populateModel(ContractAnnexParameter annex) {
        return new AssessorProfileTermsAnnexViewModel(annex, getText(annex));
    }

    private String getText(ContractAnnexParameter annex) {
        ContractResource currentContract = contractService.getCurrentContract();
        switch (annex) {
            case A:
                return currentContract.getAnnexA();
            case B:
                return currentContract.getAnnexB();
            case C:
                return currentContract.getAnnexC();
            default:
                throw new IllegalArgumentException("Unexpected annex: " + annex + ".");
        }
    }
}