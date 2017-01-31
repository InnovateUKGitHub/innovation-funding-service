package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.springframework.stereotype.Service;


@Service
public class PublicContentEligibilityFormPopulator extends AbstractContentGroupFormPopulator<EligibilityForm> implements PublicContentFormPopulator<EligibilityForm> {

    @Override
    protected EligibilityForm createInitial() {
        return new EligibilityForm();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
