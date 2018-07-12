package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Populator for the grant offer letter finance totals table
 */

@Component
public class GrantOfferLetterFinanceTotalsTablePopulator extends BaseGrantOfferLetterTablePopulator {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    public GrantOfferLetterFinanceTotalsTable createTable(Map<Organisation, List<Cost>> finances, long projectId) {

        Map<String, BigDecimal> grantClaims = getGrantClaimsForOrgs(finances.keySet(), projectId);

        Map<String, List<Cost>> orgNameFinances =
                finances
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getName(),
                                                  Map.Entry::getValue));

        Map<String, BigDecimal> totalEligibleCosts = new HashMap<>();
        orgNameFinances.forEach(
                (org, finance) ->
                        totalEligibleCosts.put(org,
                                               finance.stream()
                                                       .map(Cost::getValue)
                                                       .filter(Objects::nonNull)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add))
        );

        Map<String, BigDecimal> totalGrant = totalEligibleCosts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue()
                                                  .multiply(grantClaims.get(e.getKey()))
                                                  .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP))
                );



        List<String> industrialOrgs = finances.keySet()
                .stream()
                .filter(org -> !isAcademic(org.getOrganisationType()))
                .map(Organisation::getName)
                .collect(Collectors.toList());

        List<String> academicOrgs = finances.keySet()
                .stream()
                .filter(org -> isAcademic(org.getOrganisationType()))
                .map(Organisation::getName)
                .collect(Collectors.toList());


        BigDecimal industryTotalEligibleCosts = getTotalOfOrgs(totalEligibleCosts, industrialOrgs);

        BigDecimal academicTotalEligibleCosts = getTotalOfOrgs(totalEligibleCosts, academicOrgs);

        BigDecimal allTotalEligibleCosts = industryTotalEligibleCosts.add(academicTotalEligibleCosts);

        BigDecimal industryTotalGrant = getTotalOfOrgs(totalGrant, industrialOrgs);

        BigDecimal academicTotalGrant = getTotalOfOrgs(totalGrant, academicOrgs);

        BigDecimal allTotalGrant = industryTotalGrant.add(academicTotalGrant);

        BigDecimal industryTotalGrantClaim = industryTotalEligibleCosts.multiply(industryTotalGrant);

        BigDecimal academicTotalGrantClaim = academicTotalEligibleCosts.multiply(academicTotalGrant);

        BigDecimal allTotalGrantClaim = allTotalEligibleCosts.multiply(allTotalGrant);

        return new GrantOfferLetterFinanceTotalsTable(grantClaims,
                                                      totalEligibleCosts,
                                                      totalGrant,
                                                      industrialOrgs,
                                                      academicOrgs,
                                                      industryTotalEligibleCosts,
                                                      academicTotalEligibleCosts,
                                                      allTotalEligibleCosts,
                                                      industryTotalGrant,
                                                      academicTotalGrant,
                                                      allTotalGrant,
                                                      industryTotalGrantClaim,
                                                      academicTotalGrantClaim,
                                                      allTotalGrantClaim);
    }

    private Map<String, BigDecimal> getGrantClaimsForOrgs(Set<Organisation> organisations, long projectId) {
        return organisations.stream()
                .collect(Collectors.toMap(Organisation::getName,
                                          org -> getGrantClaimForOrg(projectId, org.getId())));
    }


    private BigDecimal getGrantClaimForOrg(long projectId, long organisationId) {
        ProjectFinance orgFinance = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        List<ProjectFinanceRow> rows = projectFinanceRowRepository.findByTargetId(orgFinance.getId());
        Optional<ProjectFinanceRow> grantClaimRow = simpleFindFirst(rows,
                                                                    pfr -> "grant-claim".equals(pfr.getName()));

        return grantClaimRow
                .map(row -> BigDecimal.valueOf(row.getQuantity()))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal averageGrantCosts()

    private BigDecimal getTotalOfOrgs(Map<String, BigDecimal> finances, List<String> orgs) {
        return finances
                .entrySet()
                .stream()
                .filter(entry -> orgs.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
