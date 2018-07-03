package org.innovateuk.ifs.project.grantofferletter.model;


import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Creates the grant offer letter totals finance table, used by the html renderer for the grant offer letter
 */

@Component
public class GrantOfferLetterFinanceTotalsTable extends GrantOfferLetterFinanceTable {


    public void populate(Map<String, List<ProjectFinanceRow>> financials) {

    }

}
