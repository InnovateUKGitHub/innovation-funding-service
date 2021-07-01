package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for GrantTermsAndConditions
 */
public interface GrantTermsAndConditionsRepository extends CrudRepository<GrantTermsAndConditions, Long> {

    String DEFAULT_TEMPLATE_NAME = "default-terms-and-conditions";

    String FIND_LATEST_VERSION = "SELECT t1 " +
            "FROM GrantTermsAndConditions t1 " +
            "WHERE version=(SELECT MAX(t2.version) FROM GrantTermsAndConditions t2 WHERE " +
            "t1.name=t2.name)" +
            "ORDER BY t1.name";

    GrantTermsAndConditions findOneByTemplate(String templateName);

    @Query(FIND_LATEST_VERSION)
    List<GrantTermsAndConditions> findLatestVersions();

    GrantTermsAndConditions findFirstByNameOrderByVersionDesc(String name);

    default GrantTermsAndConditions getLatestForFundingType(FundingType type) {
        return findFirstByNameOrderByVersionDesc(type.getDefaultTermsName());
    }
}