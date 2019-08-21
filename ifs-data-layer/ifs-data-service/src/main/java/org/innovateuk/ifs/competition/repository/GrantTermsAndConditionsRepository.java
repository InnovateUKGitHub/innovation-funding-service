package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GrantTermsAndConditions
 */
public interface GrantTermsAndConditionsRepository extends CrudRepository<GrantTermsAndConditions, Long> {

    String DEFAULT_TEMPLATE_NAME = "default-terms-and-conditions";

    String FIND_LATEST_VERSION = "SELECT t1 " +
            "FROM TermsAndConditions t1 " +
            "WHERE type='GRANT' AND version=(SELECT MAX(t2.version) FROM TermsAndConditions t2 WHERE " +
            "t1.name=t2.name)";

    GrantTermsAndConditions findOneByTemplate(String templateName);

    @Query(FIND_LATEST_VERSION)
    List<GrantTermsAndConditions> findLatestVersions();

    Optional<GrantTermsAndConditions> findFirstByNameOrderByVersionDesc(String name);

    default Optional<GrantTermsAndConditions> getLatestForFundingType(FundingType type) {
        return findFirstByNameOrderByVersionDesc(type.getDefaultTermsName());
    }
}