package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationSummarisationServiceImpl implements ApplicationSummarisationService {

    @Autowired
    private ApplicationFinanceService financeService;

    @Override
    public ServiceResult<BigDecimal> getTotalProjectCost(Application application) {

        if (application.getApplicationFinances() == null || application.getApplicationFinances().isEmpty()) {
            return result(BigDecimal.ZERO);
        }

        ServiceResult<List<ApplicationFinanceResource>> financeTotalsResult = financeService.financeTotals(application.getId());

        BigDecimal total;
        if (financeTotalsResult.isSuccess()) {
            total = financeTotalsResult.getSuccess().stream().map(t -> t.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            total = BigDecimal.ZERO;
        }

        return result(total);
    }

    @Override
    public ServiceResult<BigDecimal> getProjectTotalFunding(Long appId) {

        return financeService.financeTotals(appId).andOnSuccessReturn(financeTotalsResult -> {
            BigDecimal total = financeTotalsResult.stream().findFirst().get().getFinanceOrganisationDetails().entrySet().stream()
                    .filter(x -> totalProjectCostTypes(x.getKey())).map(Map.Entry::getValue)
                    .map(t -> t.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
            return total;
        });


    }

    @Override
    public ServiceResult<BigDecimal> getProjectOtherFunding(Long appId) {

        return financeService.financeTotals(appId).andOnSuccessReturn(financeTotalsResult -> {
            BigDecimal total = financeTotalsResult.stream().findFirst().get().getFinanceOrganisationDetails().get(FinanceRowType.OTHER_FUNDING).getTotal();
            return total;
        });


    }


    @Override
    public ServiceResult<String> getProjectLocation(Long appId) {

        return financeService.financeTotals(appId).andOnSuccessReturn(financeTotalsResult -> {
            return financeTotalsResult.stream().findFirst().get().getWorkPostcode();
        });
    }


    @Override
    public ServiceResult<BigDecimal> getFundingSought(Application application) {

        if (application.getApplicationFinances() == null || application.getApplicationFinances().isEmpty()) {
            return result(BigDecimal.ZERO);
        }

        ServiceResult<List<ApplicationFinanceResource>> financeTotalsResult = financeService.financeTotals(application.getId());

        BigDecimal fundingSought;
        if (financeTotalsResult.isSuccess()) {
            fundingSought = financeTotalsResult.getSuccess()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(BaseFinanceResource::getTotalFundingSought)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            fundingSought = BigDecimal.ZERO;
        }
        return result(fundingSought);
    }


    private ServiceResult<BigDecimal> result(BigDecimal value) {
        return serviceSuccess(value.setScale(2, RoundingMode.HALF_UP));
    }

    private boolean totalProjectCostTypes(FinanceRowType t) {
        return t.equals(FinanceRowType.LABOUR)
                || t.equals(FinanceRowType.PERSONNEL)
                || t.equals(FinanceRowType.OVERHEADS)
                || t.equals(FinanceRowType.HECP_INDIRECT_COSTS)
                || t.equals(FinanceRowType.MATERIALS)
                || t.equals(FinanceRowType.EQUIPMENT)
                || t.equals(FinanceRowType.CAPITAL_USAGE)
                || t.equals(FinanceRowType.OTHER_GOODS)
                || t.equals(FinanceRowType.SUBCONTRACTING_COSTS)
                || t.equals(FinanceRowType.TRAVEL)
                || t.equals(FinanceRowType.OTHER_COSTS);
    }
}
