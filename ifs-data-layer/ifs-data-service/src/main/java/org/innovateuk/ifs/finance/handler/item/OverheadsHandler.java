package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.transactional.OverheadFileService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Handles the overheads, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class OverheadsHandler extends FinanceRowHandler<Overhead> {
    public static final String COST_KEY = "overhead";

    @Autowired
    private OverheadFileService overheadFileService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void validate(@NotNull Overhead overhead, @NotNull BindingResult bindingResult) {

        switch (overhead.getRateType()) {
            case DEFAULT_PERCENTAGE:
                super.validate(overhead, bindingResult, Overhead.RateNotZero.class);
                break;
            case TOTAL:
                validateFilePresent(overhead, bindingResult);
                super.validate(overhead, bindingResult, Overhead.TotalCost.class);
                break;
            case NONE:
                super.validate(overhead, bindingResult);
                break;
            case HORIZON_2020_TOTAL:
                super.validate(overhead, bindingResult, Overhead.TotalCost.class);
                break;
        }
    }

    @Override
    public ApplicationFinanceRow toCost(Overhead overhead) {
        final String rateType = overhead.getRateType() != null ? overhead.getRateType().toString() : null;
        return new ApplicationFinanceRow(overhead.getId(), COST_KEY, rateType, "", overhead.getRate(), null, null, null);
    }

    @Override
    public ProjectFinanceRow toProjectCost(Overhead overhead) {
        final String rateType = overhead.getRateType() != null ? overhead.getRateType().toString() : null;
        return new ProjectFinanceRow(overhead.getId(), COST_KEY, rateType, "", overhead.getRate(), null, null, null);
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost) {
        return new Overhead(cost.getId(), OverheadRateType.valueOf(cost.getItem()), cost.getQuantity());
    }

    private void validateFilePresent(Overhead overhead, BindingResult bindingResult) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if (user != null && !user.isInternalUser()) {
            if (!overheadFileService.getFileEntryDetails(overhead.getId()).getOptionalSuccessObject().isPresent()) {
                bindingResult.reject(Overhead.FINANCE_OVERHEAD_FILE_REQUIRED);
            }
        }
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        return singletonList(initializeAcceptRate());
    }

    private ApplicationFinanceRow initializeAcceptRate() {
        Overhead costItem = new Overhead();
        ApplicationFinanceRow cost = toCost(costItem);
        cost.setDescription(OverheadCostCategory.ACCEPT_RATE);
        return cost;
    }
}
