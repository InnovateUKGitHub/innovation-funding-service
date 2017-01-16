package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.sectionupdater.ApplicationFinanceUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for getting the right updaters, and future populators
 */
@Service
public class ApplicationFinanceServiceImpl implements ApplicationFinanceService {

    private Map<SectionType, ApplicationFinanceUpdater> financeSectionSavers;

    @Autowired
    public void setFinanceSectionSavers(Collection<ApplicationFinanceUpdater> sectionSavers) {
        financeSectionSavers = sectionSavers.stream().collect(Collectors.toMap(p -> p.getRelatedSection(), Function.identity()));
    }

    @Override
    public Optional<ApplicationFinanceUpdater> getApplicationFinanceSaver(SectionType sectionType) {
        return Optional.ofNullable(financeSectionSavers.get(sectionType));
    }
}
