package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.form.section.HowToApplyForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.springframework.stereotype.Service;


@Service
public class HowToApplyFormPopulator extends AbstractContentGroupFormPopulator<HowToApplyForm> implements PublicContentFormPopulator<HowToApplyForm> {

    @Override
    protected HowToApplyForm createInitial() {
        return new HowToApplyForm();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.HOW_TO_APPLY;
    }
}
