package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * A view model for displaying project finance rows and changes made by internal project finance team
 */
public class ProjectFinanceChangesViewModel {
    private FinanceCheckEligibilityResource eligibilityOverview;
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String projectName;
    private String applicationId;
    private Long projectId;
    private Long organisationId;

    Map<FinanceRowType, BigDecimal> sectionChanges;

    Map<FinanceRowType, List<Pair<FinanceRowItem, FinanceRowItem>>> financeRowDiffPairsByType;
}
