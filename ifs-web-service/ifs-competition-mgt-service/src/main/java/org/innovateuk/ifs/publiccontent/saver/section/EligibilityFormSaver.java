package org.innovateuk.ifs.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.EligibilityForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.springframework.stereotype.Service;

/**
 * Saver for the EligibilityForm form.
 */
@Service
public class EligibilityFormSaver extends AbstractContentGroupFormSaver<EligibilityForm> implements PublicContentFormSaver<EligibilityForm> {

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }

}
