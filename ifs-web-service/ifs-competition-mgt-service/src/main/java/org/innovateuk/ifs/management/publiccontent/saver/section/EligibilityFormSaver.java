package org.innovateuk.ifs.management.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.form.section.EligibilityForm;
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
