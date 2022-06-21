package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.HecpIndirectCosts;
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
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.HECP_INDIRECT_COSTS;

/**
 * Handles the hecpIndirectCosts, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class HecpIndirectCostsHandler extends FinanceRowHandler<HecpIndirectCosts> {
    public static final String COST_KEY = "hecpIndirectCosts";

    @Autowired
    private OverheadFileService overheadFileService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void validate(@NotNull HecpIndirectCosts hecpIndirectCosts, @NotNull BindingResult bindingResult) {

        switch (hecpIndirectCosts.getRateType()) {
            case DEFAULT_PERCENTAGE:
                super.validate(hecpIndirectCosts, bindingResult, HecpIndirectCosts.RateNotZero.class);
                break;
            case TOTAL:
                validateFilePresent(hecpIndirectCosts, bindingResult);
                super.validate(hecpIndirectCosts, bindingResult, HecpIndirectCosts.TotalCost.class);
                break;
            case NONE:
                super.validate(hecpIndirectCosts, bindingResult);
                break;
            case HORIZON_2020_TOTAL:
            case HORIZON_EUROPE_GUARANTEE_TOTAL:
                super.validate(hecpIndirectCosts, bindingResult, HecpIndirectCosts.TotalCost.class);
                break;
        }
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(HecpIndirectCosts hecpIndirectCosts) {
        final String rateType = hecpIndirectCosts.getRateType() != null ? hecpIndirectCosts.getRateType().toString() : null;
        return new ApplicationFinanceRow(hecpIndirectCosts.getId(), COST_KEY, rateType, "", hecpIndirectCosts.getRate(), null, null, hecpIndirectCosts.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(HecpIndirectCosts hecpIndirectCosts) {
        final String rateType = hecpIndirectCosts.getRateType() != null ? hecpIndirectCosts.getRateType().toString() : null;
        return new ProjectFinanceRow(hecpIndirectCosts.getId(), COST_KEY, rateType, "", hecpIndirectCosts.getRate(), null, null, hecpIndirectCosts.getCostType());
    }

    @Override
    public HecpIndirectCosts toResource(FinanceRow cost) {
        return new HecpIndirectCosts(cost.getId(), OverheadRateType.valueOf(cost.getItem()), cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(HECP_INDIRECT_COSTS);
    }

    private void validateFilePresent(HecpIndirectCosts hecpIndirectCosts, BindingResult bindingResult) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if (user != null && !user.isInternalUser()) {
            if (!overheadFileService.getFileEntryDetails(hecpIndirectCosts.getId()).getOptionalSuccessObject().isPresent()) {
                bindingResult.reject(hecpIndirectCosts.FINANCE_HECP_INDIRECT_COSTS_FILE_REQUIRED);
            }
        }
    }

    protected List<HecpIndirectCosts> initialiseCosts(Finance finance) {
        return newArrayList(new HecpIndirectCosts(finance.getId()));
    }

}
