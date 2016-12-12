package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileTermsViewModel;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.resource.ProfileContractResource;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Terms view.
 */
@Component
public class AssessorProfileTermsModelPopulator {

    public AssessorProfileTermsViewModel populateModel(ProfileContractResource profileContract) {
        ContractResource contract = profileContract.getContract();

        AssessorProfileTermsViewModel model = new AssessorProfileTermsViewModel();
        model.setCurrentAgreement(profileContract.isCurrentAgreement());
        model.setContractSignedDate(profileContract.getContractSignedDate());
        model.setText(contract.getText());

        return model;
    }
}
