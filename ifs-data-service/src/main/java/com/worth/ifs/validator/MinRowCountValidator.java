package com.worth.ifs.validator;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.ValidationMessages.reject;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class MinRowCountValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ArrayList.class.equals(clazz);
    }
    private static final Log LOG = LogFactory.getLog(MinRowCountValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        List<FinanceRowItem> response = (List<FinanceRowItem>) target;

        int rowCount = 0;
        if(response.size() > 1) {
            for (final FinanceRowItem row : response) {
                boolean exclude = row.excludeInRowCount();
                if (!exclude) {
                    rowCount++;
                }
            }
        }

        if(rowCount < response.get(0).getMinRows()){
            switch(response.get(0).getCostType()) {
                case OTHER_FUNDING:
                    if(((OtherFunding)response.get(0)).getOtherPublicFunding().equals("Yes")) {
                        if(response.get(0).getMinRows() == 1){
                            reject(errors, "validation.finance.min.row.other.funding.single");
                        }else{
                            reject(errors, "validation.finance.min.row.other.funding.multiple", response.get(0).getMinRows());
                        }

                    }
                    break;
                default:
                    reject(errors, "validation.finance.min.row", response.get(0).getMinRows());
                    break;
            }
        }
    }
}
