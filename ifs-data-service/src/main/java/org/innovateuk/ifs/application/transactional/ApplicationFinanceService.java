package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.sectionupdater.ApplicationFinanceUpdater;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Service for getting the right updater based on sectionType
 */
public interface ApplicationFinanceService {
    @PreAuthorize("hasAuthority('applicant')")
    Optional<ApplicationFinanceUpdater> getApplicationFinanceSaver(SectionType sectionType);
}
