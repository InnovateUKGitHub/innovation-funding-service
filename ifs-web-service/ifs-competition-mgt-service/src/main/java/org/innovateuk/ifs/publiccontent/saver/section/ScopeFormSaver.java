package org.innovateuk.ifs.publiccontent.saver.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.ScopeForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
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
