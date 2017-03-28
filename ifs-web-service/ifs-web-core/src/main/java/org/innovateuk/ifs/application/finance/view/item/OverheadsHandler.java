package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.OverheadRateType.CUSTOM_RATE;
import static org.innovateuk.ifs.finance.resource.cost.OverheadRateType.CUSTOM_AMOUNT;

/**
 * Handles the conversion of form fields to overhead
 */
public class OverheadsHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        Optional<FinanceFormField> rateTypeField = financeFormFields.stream().filter(
                financeFormField -> financeFormField.getCostName().equals("type")).
                findFirst();
        Optional<FinanceFormField> rateValueField = determineRateValueField(rateTypeField, financeFormFields);

        if(id == null && !rateTypeField.isPresent() && !rateValueField.isPresent()) {
        	return null;
        }
        
        return createOverHead(id, rateTypeField, rateValueField);
    }

    private Optional<FinanceFormField> determineRateValueField(Optional<FinanceFormField> rateTypeField, List<FinanceFormField> financeFormFields) {
        if(!rateTypeField.isPresent()) {
            return financeFormFields.stream().findFirst();
        }
        else {
            final String fieldNameFinal = getCorrespondingFieldNameForType(rateTypeField.get().getValue());

            return financeFormFields.stream().filter(
                    financeFormField -> financeFormField.getCostName().equals(fieldNameFinal)).
                    findFirst();
        }
    }

    private String getCorrespondingFieldNameForType(String rateTypeName) {
        if(rateTypeName.equals(CUSTOM_RATE.name())) {
            return "customRate";
        }
        else if(rateTypeName.equals(CUSTOM_AMOUNT.name())) {
            return  "total";
        }

        return null;
    }

    protected FinanceRowItem createOverHead(Long id, Optional<FinanceFormField> rateType, Optional<FinanceFormField> customRate) {
        OverheadRateType overheadRateType = null;
        Integer rate = null;

        if(rateType.isPresent() && customRate.isPresent()) {
            overheadRateType = OverheadRateType.valueOf(rateType.get().getValue());
            rate = handleRate(overheadRateType, Integer.valueOf(customRate.get().getValue()));
        }
        else if(rateType.isPresent() && !customRate.isPresent()) {
            overheadRateType = OverheadRateType.valueOf(rateType.get().getValue());
            rate = handleRate(overheadRateType, 0);
        } else if(!rateType.isPresent() && customRate.isPresent()) {
            rate = Integer.valueOf(customRate.get().getValue());
        }

        return new Overhead(id, overheadRateType, rate);
    }

    private Integer handleRate(OverheadRateType rateType, Integer customRate) {
        switch(rateType) {
            case DEFAULT_PERCENTAGE:
                return OverheadRateType.DEFAULT_PERCENTAGE.getRate();
            case CUSTOM_RATE:
            case CUSTOM_AMOUNT:
                return customRate;
            case NONE:
            default:
                return 0;
        }
    }
}
