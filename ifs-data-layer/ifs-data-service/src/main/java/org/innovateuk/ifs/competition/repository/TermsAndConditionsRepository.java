package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.TermsAndConditions;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for TermsAndConditions
 */
public interface TermsAndConditionsRepository extends CrudRepository<TermsAndConditions, Long> {

    public static final String DEFAULT_TEMPLATE_NAME = "default-terms-and-conditions";

    TermsAndConditions findOneByTemplate(String templateName);
}
