package org.innovateuk.ifs.application.transactional.sectionupdater;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.SectionType;

/**
 * Handle specific actions when this part gets save in the applicationForm
 */

public interface ApplicationFinanceUpdater {

    SectionType getRelatedSection();
    void handleMarkAsInComplete(Application currentApplication, Section currentSection, Long processRoleId);
    void handleMarkAsComplete(Application currentApplication, Section currentSection, Long processRoleId);
}
