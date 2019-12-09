package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
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
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OVERHEADS;

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
    public ApplicationFinanceRow toApplicationDomain(Overhead overhead) {
        final String rateType = overhead.getRateType() != null ? overhead.getRateType().toString() : null;
        return new ApplicationFinanceRow(overhead.getId(), COST_KEY, rateType, "", overhead.getRate(), null, null, overhead.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(Overhead overhead) {
        final String rateType = overhead.getRateType() != null ? overhead.getRateType().toString() : null;
        return new ProjectFinanceRow(overhead.getId(), COST_KEY, rateType, "", overhead.getRate(), null, null, overhead.getCostType());
    }

    @Override
    public Overhead toResource(FinanceRow cost) {
        return new Overhead(cost.getId(), OverheadRateType.valueOf(cost.getItem()), cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(OVERHEADS);
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

    protected Optional<Overhead> intialiseCost(Finance finance) {
        return Optional.of(new Overhead(finance.getId()));
    }

}
