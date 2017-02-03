package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;

import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;

import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the overheads, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OverheadsHandler extends FinanceRowHandler {
    public static final String COST_KEY = "overhead";

    @Autowired
    private FileEntryService fileEntryService;

    @Override
    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {

        Overhead overhead = (Overhead) costItem;
        switch (overhead.getRateType()) {
            case DEFAULT_PERCENTAGE:
            case CUSTOM_RATE:
                super.validate(costItem, bindingResult, Overhead.RateNotZero.class);
                break;
            case TOTAL:
                super.validate(costItem, bindingResult, Overhead.TotalCost.class);
                break;
            case NONE:
                super.validate(costItem, bindingResult);
                break;
        }
    }

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof Overhead) {
            Overhead overhead = (Overhead) costItem;
            Integer rate = overhead.getRate();
            String rateType = null;

            if (overhead.getRateType() != null) {
                rateType = overhead.getRateType().toString();
            }
            cost = new ApplicationFinanceRow(overhead.getId(), COST_KEY, rateType, "", rate, null, null, null);
        }
        return cost;
    }

    @Override
    public ProjectFinanceRow toProjectCost(FinanceRowItem costItem) {
        ProjectFinanceRow cost = null;
        if (costItem instanceof Overhead) {
            Overhead overhead = (Overhead) costItem;
            Integer rate = overhead.getRate();
            String rateType = null;

            if (overhead.getRateType() != null) {
                rateType = overhead.getRateType().toString();
            }
            cost = new ProjectFinanceRow(overhead.getId(), COST_KEY, rateType, "", rate, null, null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    private FinanceRowItem buildRowItem(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues){
        OverheadRateType type = OverheadRateType.valueOf(cost.getItem());
        Overhead overhead = new Overhead(cost.getId(), type, cost.getQuantity());

        Optional<FinanceRowMetaValue> useTotalOptionMetaValue = financeRowMetaValues.stream().
                filter(metaValue -> metaValue.getFinanceRowMetaField().getTitle().equals(OverheadCostCategory.USE_TOTAL_META_FIELD)).
                findFirst();

        if (useTotalOptionMetaValue.isPresent() && useTotalOptionMetaValue.get().getValue().equals("false")) {
            overhead.setUseTotalOption(false);
        } else {
            overhead.setUseTotalOption(true);
            addOptionalCalculationFile(cost, financeRowMetaValues, overhead);
        }

        return overhead;
    }

    private void addOptionalCalculationFile(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues, Overhead overhead) {
        Optional<FinanceRowMetaValue> overheadFileMetaValue = financeRowMetaValues.stream().
                filter(metaValue -> metaValue.getFinanceRowMetaField().getTitle().equals(OverheadCostCategory.CALCULATION_FILE_FIELD)).
                findFirst();

        overheadFileMetaValue.ifPresent(financeRowMetaValue -> fileEntryService.findOne(Long.valueOf(financeRowMetaValue.getValue())).
                andOnSuccessReturnVoid(overhead::setCalculationFile));
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeAcceptRate());

        return costs;
    }

    private ApplicationFinanceRow initializeAcceptRate() {
        Overhead costItem = new Overhead();
        ApplicationFinanceRow cost = toCost(costItem);
        cost.setDescription(OverheadCostCategory.ACCEPT_RATE);
        return cost;
    }
}
