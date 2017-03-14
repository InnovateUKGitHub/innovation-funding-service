package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.controller.profile.AssessorProfileAgreementController.AgreementAnnexParameter;
import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileAgreementAnnexViewModel;
import org.innovateuk.ifs.agreement.service.AgreementService;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Profile Agreement Annex view.
 */
@Component
public class AssessorProfileAgreementAnnexModelPopulator {

    @Autowired
    private AgreementService agreementService;

    public AssessorProfileAgreementAnnexViewModel populateModel(AgreementAnnexParameter annex) {
        return new AssessorProfileAgreementAnnexViewModel(annex, getText(annex));
    }

    private String getText(AgreementAnnexParameter annex) {
        AgreementResource agreementResource = agreementService.getCurrentAgreement();
        switch (annex) {
            case A:
                return agreementResource.getAnnexA();
            case B:
                return agreementResource.getAnnexB();
            case C:
                return agreementResource.getAnnexC();
            default:
                throw new IllegalArgumentException("Unexpected annex: " + annex + ".");
        }
    }
}
