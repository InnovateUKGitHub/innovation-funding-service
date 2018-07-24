package org.innovateuk.ifs.application.populator.finance.view;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Configurable
public class OrganisationProjectFinanceOverviewImpl implements OrganisationFinanceOverview {

    private Long projectId;
    private List<ProjectFinanceResource> projectFinances = new ArrayList<>();

    private ProjectFinanceRestService projectFinanceRestService;

    public OrganisationProjectFinanceOverviewImpl() {
        // no-arg constructor
    }

    public OrganisationProjectFinanceOverviewImpl(ProjectFinanceRestService projectFinanceRestService, Long projectId) {
        this.projectId = projectId;
        this.projectFinanceRestService = projectFinanceRestService;
        initializeOrganisationFinances();
    }

    private void initializeOrganisationFinances() {
        projectFinanceRestService.getFinanceTotals(projectId).handleSuccessOrFailure(
                failure -> Collections.<ProjectFinanceResource>emptyList(),
                success -> success
        );
    }

    public Map<Long, BaseFinanceResource> getFinancesByOrganisation() {
        return projectFinances
                .stream()
                .collect(Collectors.toMap(ProjectFinanceResource::getOrganisation, f -> f));
    }

    public Map<FinanceRowType, BigDecimal> getTotalPerType() {
        Map<FinanceRowType, BigDecimal> totalPerType = new EnumMap<>(FinanceRowType.class);
        for (FinanceRowType costType : FinanceRowType.values()) {
            BigDecimal typeTotal = projectFinances.stream()
                    .filter(o -> o.getFinanceOrganisationDetails(costType) != null)
                    .map(o -> o.getFinanceOrganisationDetails(costType).getTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPerType.put(costType, typeTotal);
        }

        return totalPerType;
    }

    public BigDecimal getTotal() {
        return projectFinances.stream()
                .map(of -> of.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFundingSought() {
        BigDecimal totalFundingSought = projectFinances.stream()
                .filter(of -> of != null && of.getGrantClaimPercentage() != null)
                .map(of -> of.getTotalFundingSought())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalFundingSought;
    }

    public BigDecimal getTotalContribution() {
        return projectFinances.stream()
                .filter(of -> of != null)
                .map(of -> of.getTotalContribution())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOtherFunding() {
        return projectFinances.stream()
                .filter(of -> of != null)
                .map(of -> of.getTotalOtherFunding())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
