package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class GrantOfferLetterAcademicFinanceTable implements GrantOfferLetterFinanceTable {

    private Map<String, BigDecimal> thing;

    @Override
    public void populate(Map<String, List<ProjectFinanceRow>> financials) {

    }
}
