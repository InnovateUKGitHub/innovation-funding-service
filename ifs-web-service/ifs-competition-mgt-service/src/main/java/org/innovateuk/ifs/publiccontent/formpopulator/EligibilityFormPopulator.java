package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.springframework.stereotype.Service;


@Service
public class EligibilityFormPopulator extends AbstractPublicContentFormPopulator<EligibilityForm> implements PublicContentFormPopulator<EligibilityForm> {

    @Override
    protected EligibilityForm createInitial() {
        return new EligibilityForm();
    }

    @Override
    protected void populateSection(EligibilityForm form, PublicContentResource publicContentResource) {
        form.setGroups(getContentGroupForms(publicContentResource));
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
