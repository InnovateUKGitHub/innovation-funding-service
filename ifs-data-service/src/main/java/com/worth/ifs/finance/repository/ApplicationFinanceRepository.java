package com.worth.ifs.finance.repository;

import com.worth.ifs.finance.domain.ApplicationFinance;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationFinanceRepository extends PagingAndSortingRepository<ApplicationFinance, Long> {
    ApplicationFinance findByApplicationIdAndOrganisationId(@Param("applicationId") Long applicationId, @Param("organisationId") Long organisationId);
    List<ApplicationFinance> findByApplicationId(@Param("applicationId") Long applicationId);

}
