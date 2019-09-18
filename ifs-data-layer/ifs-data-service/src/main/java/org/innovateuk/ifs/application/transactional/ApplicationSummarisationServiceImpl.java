package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

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
}
