package org.innovateuk.ifs.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.HowToApplyForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.springframework.stereotype.Service;

/**
 * Saver for the HowToApplyForm form.
 */
@Service
public class HowToApplyFormSaver extends AbstractContentGroupFormSaver<HowToApplyForm> implements PublicContentFormSaver<HowToApplyForm> {

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.HOW_TO_APPLY;
    }

}
