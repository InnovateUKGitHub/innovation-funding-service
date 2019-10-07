package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrowthTableRepository extends CrudRepository<GrowthTable, Long> {
}
