package org.innovateuk.ifs.application.transactional.sectionupdater;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.security.NotSecured;

/**
 * Handle specific actions when this part gets save in the applicationForm
 */

public interface ApplicationFinanceUpdater {

    @NotSecured("Intern service")
    SectionType getRelatedSection();

    @NotSecured("Intern service")
    void handleMarkAsInComplete(Application currentApplication, Section currentSection, Long processRoleId);

    @NotSecured("Intern service")
    void handleMarkAsComplete(Application currentApplication, Section currentSection, Long processRoleId);
}
