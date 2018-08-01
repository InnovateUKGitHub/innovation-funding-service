package org.innovateuk.ifs.project.grantofferletter.model;

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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Populator for the grant offer letter finance totals table
 */

@Component
public class GrantOfferLetterFinanceTotalsTablePopulator extends BaseGrantOfferLetterTablePopulator {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    public static final String GRANT_CLAIM_IDENTIFIER = "grant-claim";

    public GrantOfferLetterFinanceTotalsTable createTable(Map<Organisation, List<Cost>> finances, long projectId) {

        Map<String, BigDecimal> grantClaims = getGrantClaimsForOrgs(finances.keySet(), projectId);

        Map<String, List<Cost>> orgNameFinances =
                simpleToMap(finances.entrySet(),
                            e -> e.getKey().getName(),
                            Map.Entry::getValue);

        Map<String, BigDecimal> totalEligibleCosts = new HashMap<>();
        orgNameFinances.forEach((org, finance) ->
                                        totalEligibleCosts.put(org,
                                                               finance
                                                                       .stream()
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

        BigDecimal industryTotalGrantClaim = industryTotalEligibleCosts.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO :
                industryTotalGrant
                        .divide(industryTotalEligibleCosts,2, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

        // Grant claim is always 100% for academic organisations
        BigDecimal academicTotalGrantClaim = BigDecimal.valueOf(100);

        BigDecimal allTotalGrantClaim = allTotalEligibleCosts.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO :
                allTotalGrant
                        .divide(allTotalEligibleCosts,2, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

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

        return simpleToMap(organisations,
                           Organisation::getName,
                           org -> getGrantClaimForOrg(projectId, org));
    }


    private BigDecimal getGrantClaimForOrg(long projectId, Organisation organisation) {
        if(isAcademic(organisation.getOrganisationType())) {

            // Grant claim is always 100% for academic organisations
            return BigDecimal.valueOf(100);
        } else {
            
            ProjectFinance orgFinance = projectFinanceRepository.findByProjectIdAndOrganisationId(
                    projectId,
                    organisation.getId()
            );
            List<ProjectFinanceRow> rows = projectFinanceRowRepository.findByTargetId(orgFinance.getId());
            Optional<ProjectFinanceRow> grantClaimRow = simpleFindFirst(
                    rows,
                    pfr -> GRANT_CLAIM_IDENTIFIER.equals(pfr.getName())
            );

            return grantClaimRow
                    .map(row -> BigDecimal.valueOf(row.getQuantity()))
                    .orElse(BigDecimal.ZERO);
        }
    }

    private BigDecimal getTotalOfOrgs(Map<String, BigDecimal> finances, List<String> orgs) {
        return finances
                .entrySet()
                .stream()
                .filter(entry -> orgs.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
