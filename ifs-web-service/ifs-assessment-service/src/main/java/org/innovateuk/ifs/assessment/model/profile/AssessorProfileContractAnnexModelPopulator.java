package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.controller.profile.AssessorProfileContractController.ContractAnnexParameter;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileContractAnnexViewModel;
import org.innovateuk.ifs.contract.service.ContractService;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Profile Terms of Contract Annex view.
 */
@Component
public class AssessorProfileContractAnnexModelPopulator {

    @Autowired
    private ContractService contractService;

    public AssessorProfileContractAnnexViewModel populateModel(ContractAnnexParameter annex) {
        return new AssessorProfileContractAnnexViewModel(annex, getText(annex));
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
