package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.organisation.domain.Organisation;

import java.util.List;
import java.util.Map;

/**
 * Interface for grant offer letter finance tables
 **/
public interface GrantOfferLetterFinanceTablePopulatorInterface {

    GrantOfferLetterFinanceTable createTable(Map<Organisation, List<ProjectFinanceRow>> finances);
}
