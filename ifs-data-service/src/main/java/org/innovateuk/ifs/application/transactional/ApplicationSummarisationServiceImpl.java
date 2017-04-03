package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationSummarisationServiceImpl implements ApplicationSummarisationService {

	@Autowired
	private FinanceRowService financeRowService;

	@Override
	public ServiceResult<BigDecimal> getTotalProjectCost(Application application) {

		if (application.getApplicationFinances() == null || application.getApplicationFinances().isEmpty()) {
			return result(BigDecimal.ZERO);
		}

		ServiceResult<List<ApplicationFinanceResource>> financeTotalsResult = financeRowService.financeTotals(application.getId());

		BigDecimal total;
		if (financeTotalsResult.isSuccess()) {
			total = financeTotalsResult.getSuccessObject().stream().map(t -> t.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
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

		ServiceResult<List<ApplicationFinanceResource>> financeTotalsResult = financeRowService.financeTotals(application.getId());

		BigDecimal fundingSought;
		if (financeTotalsResult.isSuccess()) {
			fundingSought = financeTotalsResult.getSuccessObject().stream()
					.filter(of -> of != null && of.getGrantClaimPercentage() != null)
					.map(of -> of.getTotalFundingSought()).reduce(BigDecimal.ZERO, BigDecimal::add);
		} else {
			fundingSought = BigDecimal.ZERO;
		}
		return result(fundingSought);
	}
	
	private ServiceResult<BigDecimal> result(BigDecimal value) {
		return serviceSuccess(value.setScale(2, RoundingMode.HALF_UP));
	}
}
