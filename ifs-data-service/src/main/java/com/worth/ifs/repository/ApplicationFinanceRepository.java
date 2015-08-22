package com.worth.ifs.repository;

import com.worth.ifs.domain.ApplicationFinance;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "applicationFinance", path = "applicationfinance")
public interface ApplicationFinanceRepository extends PagingAndSortingRepository<ApplicationFinance, Long> {
    ApplicationFinance findByApplicationIdAndOrganisationId(@Param("applicationId") Long applicationId, @Param("organisationId") Long organisationId);

}
