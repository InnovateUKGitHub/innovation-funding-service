package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.domain.KtpFinancialYears;
import org.springframework.data.repository.CrudRepository;

public interface KtpFinancialYearsRepository extends CrudRepository<KtpFinancialYears, Long> {
}
