package org.innovateuk.ifs.util;

import org.apache.commons.lang3.BooleanUtils;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A util class to provide common ktp fec model services
 */
@Component
public class KtpFecFilter {

    @Value("${ifs.ktp.fec.finance.model.enabled}")
    private boolean fecFinanceModel;

    public List<? extends FinanceRow> filterKtpFecCostCategoriesIfRequired(Finance finance, List<? extends FinanceRow> financeRows) {
        if (fecFinanceModel && finance.getApplication().getCompetition().isKtp()) {
            financeRows = financeRows.stream()
                    .filter(financeRow -> BooleanUtils.isFalse(finance.getFecModelEnabled())
                            ? !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRow.getType())
                            : !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRow.getType()))
                    .collect(Collectors.toList());
        }

        return financeRows;
    }
}
