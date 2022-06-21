package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherGoods;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_GOODS;

/**
 * Handles the other goods costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@SuppressWarnings("unchecked")
@Component
public class OtherGoodsHandler extends FinanceRowHandler<OtherGoods> {
    public static final String COST_FIELD_EXISTING = "existing";
    public static final String COST_FIELD_RESIDUAL_VALUE = "residual_value";
    public static final String COST_FIELD_UTILISATION = "utilisation";
    public static final String COST_KEY = "other-goods";

    @Override
    public ApplicationFinanceRow toApplicationDomain(OtherGoods costItem) {
        return costItem != null ? mapOtherGoods(costItem) : null;
    }

    @Override
    public ProjectFinanceRow toProjectDomain(OtherGoods costItem) {
        return costItem != null ? mapOtherGoodsToProjectCost(costItem) : null;
    }

    @Override
    public OtherGoods toResource(FinanceRow cost) {
        return buildRowItem(cost, cost.getFinanceRowMetadata());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(OTHER_GOODS);
    }

    private OtherGoods buildRowItem(FinanceRow cost, List<FinanceRowMetaValue> financeRowMetaValues){
        String existing = "";
        BigDecimal residualValue = BigDecimal.ZERO;
        Integer utilisation = 0;

        for (FinanceRowMetaValue costValue : financeRowMetaValues) {
            if(costValue.getFinanceRowMetaField() != null && costValue.getFinanceRowMetaField().getTitle() != null){
                String title = costValue.getFinanceRowMetaField().getTitle();
                if (title.equals(COST_FIELD_EXISTING)) {
                    existing = parseString(costValue.getValue());
                } else if (title.equals(COST_FIELD_RESIDUAL_VALUE)) {
                    residualValue = parseBigDecimal(costValue.getValue());
                } else if (title.equals(COST_FIELD_UTILISATION)) {
                    utilisation = parseInteger(costValue.getValue());
                }
            }
        }

        return new OtherGoods(cost.getId(), cost.getQuantity(), cost.getDescription(), existing, cost.getCost(), residualValue, utilisation, cost.getTarget().getId());
    }

    private String parseString(String value) {
        return "null".equals(value) ? null : value;
    }

    private BigDecimal parseBigDecimal(String value) {
        String parsed = parseString(value);
        if (parsed == null) {
            return null;
        }
        return new BigDecimal(parsed);
    }

    private Integer parseInteger(String value) {
        String parsed = parseString(value);
        if (parsed == null) {
            return null;
        }
        return Integer.valueOf(parsed);
    }

    private ApplicationFinanceRow mapOtherGoods(FinanceRowItem costItem) {
        OtherGoods otherGoods = (OtherGoods) costItem;
        ApplicationFinanceRow otherGoodsCost = new ApplicationFinanceRow(otherGoods.getId(), COST_KEY, "", otherGoods.getDescription(), otherGoods.getDeprecation(),
                otherGoods.getNpv(), null, costItem.getCostType());
        otherGoodsCost.addCostValues(
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_EXISTING), otherGoods.getExisting()),
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(otherGoods.getResidualValue())),
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(otherGoods.getUtilisation())));

        return otherGoodsCost;
    }

    private ProjectFinanceRow mapOtherGoodsToProjectCost(OtherGoods otherGoods) {
        ProjectFinanceRow otherGoodsCost = new ProjectFinanceRow(otherGoods.getId(), COST_KEY, "", otherGoods.getDescription(), otherGoods.getDeprecation(),
                otherGoods.getNpv(), null, otherGoods.getCostType());
        otherGoodsCost.addCostValues(
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_EXISTING), otherGoods.getExisting()),
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_RESIDUAL_VALUE), String.valueOf(otherGoods.getResidualValue())),
                new FinanceRowMetaValue(otherGoodsCost, costFields.get(COST_FIELD_UTILISATION), String.valueOf(otherGoods.getUtilisation())));

        return otherGoodsCost;
    }
}
