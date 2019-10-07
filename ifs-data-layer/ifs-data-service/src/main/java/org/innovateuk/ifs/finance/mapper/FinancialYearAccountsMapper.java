package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.domain.FinancialYearAccounts;
import org.innovateuk.ifs.finance.domain.GrowthTable;
import org.innovateuk.ifs.finance.resource.FinancialYearAccountsResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class FinancialYearAccountsMapper extends BaseResourceMapper<FinancialYearAccounts, FinancialYearAccountsResource> {

    @Autowired
    private EmployeesAndTurnoverMapper employeesAndTurnoverMapper;

    @Autowired
    private GrowthTableMapper growthTableMapper;

    @Override
    public FinancialYearAccountsResource mapToResource(FinancialYearAccounts domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof GrowthTable) {
            return growthTableMapper.mapToResource((GrowthTable) domain);
        } else if (domain instanceof EmployeesAndTurnover) {
            return employeesAndTurnoverMapper.mapToResource((EmployeesAndTurnover) domain);
        }
        return null;
    }
}
