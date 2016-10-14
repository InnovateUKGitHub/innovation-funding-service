package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileTermsViewModel;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileContractResource;
import com.worth.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Terms view.
 */
@Component
public class AssessorProfileTermsModelPopulator {

    @Autowired
    private UserService userService;

    public AssessorProfileTermsViewModel populateModel(Long userId) {

        ProfileContractResource profileContract = getProfileContract(userId);
        ContractResource contract = profileContract.getContract();

        AssessorProfileTermsViewModel model = new AssessorProfileTermsViewModel();
        model.setCurrentAgreement(profileContract.isCurrentAgreement());
        model.setContractSignedDate(profileContract.getContractSignedDate());
        model.setText(contract.getText());
        model.setAnnexOne(contract.getAnnexOne());
        model.setAnnexTwo(contract.getAnnexTwo());
        model.setAnnexThree(contract.getAnnexThree());

        return model;
    }

    private ProfileContractResource getProfileContract(Long userId) {
        return userService.getProfileContract(userId);
    }
}
