package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.EligibilityForm;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
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
