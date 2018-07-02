package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;

import java.util.List;
import java.util.Map;

public interface GrantOfferLetterFinanceTable {

    public void populate(Map<String, List<ProjectFinanceRow>> financials);
}
