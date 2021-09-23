package org.innovateuk.ifs.project.grantofferletter.viewmodel;


import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  Holder of values for the academic finance table, used on the grant offer letter template page
 */
public class SubsidyControlModel {

    private final List<String> organisationNames;
    private final List<String> researchOrganisationNames;
    private final List<String> organisationNamesWithinNI;
    private final List<String> researchOrganisationNamesWithinNI;

    public SubsidyControlModel(List<String> organisationNames,
            List<String> researchOrganisationNames,
            List<String> organisationNamesWithinNI,
            List<String> researchOrganisationNamesWithinNI) {
        this.organisationNames = organisationNames;
        this.researchOrganisationNames = researchOrganisationNames;
        this.organisationNamesWithinNI = organisationNamesWithinNI;
        this.researchOrganisationNamesWithinNI = researchOrganisationNamesWithinNI;
    }

    public List<String> getOrganisationNames() {
        return organisationNames;
    }

    public List<String> getResearchOrganisationNames() {
        return researchOrganisationNames;
    }

    public List<String> getOrganisationNamesWithinNI() {
        return organisationNamesWithinNI;
    }

    public List<String> getResearchOrganisationNamesWithinNI() {
        return researchOrganisationNamesWithinNI;
    }
}

