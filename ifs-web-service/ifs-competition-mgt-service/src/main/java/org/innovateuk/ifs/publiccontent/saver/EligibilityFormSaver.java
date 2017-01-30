package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public class EligibilityFormSaver extends AbstractPublicContentFormSaver<EligibilityForm> implements PublicContentFormSaver<EligibilityForm> {

    @Override
    protected void populateResource(EligibilityForm form, PublicContentResource publicContentResource) {
    }


    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
