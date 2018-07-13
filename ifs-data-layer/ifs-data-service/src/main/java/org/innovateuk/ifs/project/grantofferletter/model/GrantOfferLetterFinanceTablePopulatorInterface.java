package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.financechecks.domain.Cost;

import java.util.List;
import java.util.Map;

/**
 * Interface for grant offer letter finance tables
 **/
public interface GrantOfferLetterFinanceTablePopulatorInterface {

    GrantOfferLetterFinanceTable createTable(Map<Organisation, List<Cost>> finances);
}
