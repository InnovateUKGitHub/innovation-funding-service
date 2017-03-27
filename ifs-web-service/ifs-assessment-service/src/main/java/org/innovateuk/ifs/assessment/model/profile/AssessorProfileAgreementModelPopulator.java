package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileAgreementViewModel;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Profile Agreement view.
 */
@Component
public class AssessorProfileAgreementModelPopulator {

    public AssessorProfileAgreementViewModel populateModel(ProfileAgreementResource profileAgreementResource) {
        AgreementResource agreementResource = profileAgreementResource.getAgreement();

        AssessorProfileAgreementViewModel model = new AssessorProfileAgreementViewModel();
        model.setCurrentAgreement(profileAgreementResource.isCurrentAgreement());
        model.setAgreementSignedDate(profileAgreementResource.getAgreementSignedDate());
        model.setText(agreementResource.getText());

        return model;
    }
}
