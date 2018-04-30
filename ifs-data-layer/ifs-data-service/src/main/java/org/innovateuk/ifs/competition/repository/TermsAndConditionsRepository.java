package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.TermsAndConditions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for TermsAndConditions
 */
public interface TermsAndConditionsRepository extends CrudRepository<TermsAndConditions, Long> {

    public static final String DEFAULT_TEMPLATE_NAME = "default-terms-and-conditions";

    String FIND_LATEST_VERSION = "SELECT t1 " +
            "FROM TermsAndConditions t1 " +
            "WHERE version=(SELECT MAX(version) FROM TermsAndConditions t2 WHERE t1.name=t2.name)";

    TermsAndConditions findOneByTemplate(String templateName);

    @Query(FIND_LATEST_VERSION)
    List<TermsAndConditions> findLatestVersions();
}
