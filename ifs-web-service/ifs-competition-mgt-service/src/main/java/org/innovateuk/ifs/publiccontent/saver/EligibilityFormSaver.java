package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public class EligibilityFormSaver extends AbstractContentGroupFormSaver<EligibilityForm> implements PublicContentFormSaver<EligibilityForm> {

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }

    @Override
    protected boolean canHaveEmptyGroups() {
        return false;
    }
}
