package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Populator for the grant offer letter finance totals table
 */
@Component
public class GrantOfferLetterFinanceTotalsTablePopulator extends BaseGrantOfferLetterTablePopulator implements GrantOfferLetterFinanceTablePopulatorInterface {

    @Override
    public GrantOfferLetterFinanceTotalsTable createTable(Map<Organisation, List<ProjectFinanceRow>> finances) {

        Map<String, List<ProjectFinanceRow>> orgNameFinances =
                finances
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getName(),
                                                  Map.Entry::getValue));

        Map<String, BigDecimal> grantClaims = sumByFinancialType(orgNameFinances,
                                                                 "grant-claim");

        Map<String, BigDecimal> totalEligibleCosts = new HashMap<>();
        orgNameFinances.forEach(
                (org, finance) ->
                        totalEligibleCosts.put(org,
                                               finance.stream()
                                                       .map(ProjectFinanceRow::getCost)
                                                       .filter(Objects::nonNull)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add))
        );

        Map<String, BigDecimal> totalGrant = totalEligibleCosts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue()
                                                  .multiply(grantClaims.get(e.getKey()))
                                                  .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP)));

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

    private BigDecimal getTotalOfOrgs(Map<String, BigDecimal> finances, List<String> orgs) {
        return finances
                .entrySet()
                .stream()
                .filter(entry -> orgs.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
