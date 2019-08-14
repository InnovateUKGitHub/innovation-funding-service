package org.innovateuk.ifs.management.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.form.section.ScopeForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the ScopeForm form.
 */
@Service
public class ScopeFormSaver extends AbstractContentGroupFormSaver<ScopeForm> implements PublicContentFormSaver<ScopeForm> {

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SCOPE;
    }

}
