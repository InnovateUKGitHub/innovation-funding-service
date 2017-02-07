package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.HowToApplyForm;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
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
