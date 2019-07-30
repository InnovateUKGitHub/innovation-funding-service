package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.form.section.EligibilityForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
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
