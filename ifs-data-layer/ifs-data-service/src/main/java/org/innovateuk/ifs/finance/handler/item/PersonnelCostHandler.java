package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.PersonnelCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.PersonnelCost;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.PERSONNEL;

/**
 * Handles the labour costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class PersonnelCostHandler extends FinanceRowHandler<PersonnelCost> {

    public static final String COST_KEY = "persoonel";
    public static final Integer DEFAULT_WORKING_DAYS = 232;
    public static final String THIRDPARTY_OFGEM_NAME_KEY = "third-party-ofgem";

    @Override
    public void validate(PersonnelCost personnelCost, BindingResult bindingResult) {
        if (StringUtils.isNotEmpty(personnelCost.getName()) && (personnelCost.getName().equals(PersonnelCostCategory.WORKING_DAYS_KEY) || personnelCost.getName().equals(PersonnelCostCategory.WORKING_DAYS_PER_YEAR))) {
            super.validate(personnelCost, bindingResult, PersonnelCost.YearlyWorkingDays.class);
        } else {
            super.validate(personnelCost, bindingResult);
        }
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(PersonnelCost personnelCostItem) {
        return personnelCostItem != null ? new ApplicationFinanceRow(
                                            personnelCostItem.getId(),
                                            personnelCostItem.getName(),
                                            personnelCostItem.getRole(),
                                            personnelCostItem.getDescription(),
                                            personnelCostItem.getLabourDays(),
                                            personnelCostItem.isThirdPartyOfgem() ? personnelCostItem.getRate() : personnelCostItem.getGrossEmployeeCost(),
                                            null, personnelCostItem.getCostType()) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(PersonnelCost costItem) {
        return new ProjectFinanceRow(
                    costItem.getId(),
                    costItem.getName(),
                    costItem.getRole(),
                    costItem.getDescription(),
                    costItem.getLabourDays(),
                    costItem.isThirdPartyOfgem() ? costItem.getRate() : costItem.getGrossEmployeeCost(),
                    null, costItem.getCostType());
    }

    @Override
    public PersonnelCost toResource(FinanceRow cost) {
        PersonnelCost personnelCost;

        boolean thirdPartyOfgem = cost.getTarget().getCompetition().isThirdPartyOfgem();
        if (THIRDPARTY_OFGEM_NAME_KEY.equals(cost.getName())) {
            personnelCost = new PersonnelCost(cost.getId(), cost.getName(), cost.getItem(), BigDecimal.ONE, cost.getQuantity(),
                    cost.getDescription(), cost.getTarget().getId(), cost.getCost(), thirdPartyOfgem);
        } else {
            personnelCost = new PersonnelCost(cost.getId(), cost.getName(), cost.getItem(), cost.getCost(), cost.getQuantity(),
                    cost.getDescription(), cost.getTarget().getId(), BigDecimal.ZERO, thirdPartyOfgem);
        }

        return personnelCost;
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(PERSONNEL);
    }

    @Override
    protected List<PersonnelCost> initialiseCosts(Finance finance) {
        String description = PersonnelCostCategory.WORKING_DAYS_PER_YEAR;
        boolean thirdPartyOfgem = finance.getCompetition().isThirdPartyOfgem();

        List<PersonnelCost> defaultLabourCost = Collections.emptyList();

        if (!thirdPartyOfgem) {
            defaultLabourCost = newArrayList(new PersonnelCost(null, PersonnelCostCategory.WORKING_DAYS_KEY, null,
                    null, DEFAULT_WORKING_DAYS, description, finance.getId(), null, thirdPartyOfgem));
        }

        return defaultLabourCost;
    }
}
