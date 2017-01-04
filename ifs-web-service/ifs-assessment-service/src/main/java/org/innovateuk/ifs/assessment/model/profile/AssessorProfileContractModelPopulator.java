package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileContractViewModel;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.resource.ProfileContractResource;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Profile Contract view.
 */
@Component
public class AssessorProfileContractModelPopulator {

    public AssessorProfileContractViewModel populateModel(ProfileContractResource profileContract) {
        ContractResource contract = profileContract.getContract();

        AssessorProfileContractViewModel model = new AssessorProfileContractViewModel();
        model.setCurrentAgreement(profileContract.isCurrentAgreement());
        model.setContractSignedDate(profileContract.getContractSignedDate());
        model.setText(contract.getText());

        return model;
    }
}
