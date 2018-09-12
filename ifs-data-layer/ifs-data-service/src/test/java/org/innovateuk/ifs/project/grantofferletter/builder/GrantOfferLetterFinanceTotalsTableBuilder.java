package org.innovateuk.ifs.project.grantofferletter.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for GrantOfferLetterFinanceTotalsTables.
 */

public class GrantOfferLetterFinanceTotalsTableBuilder extends BaseBuilder<GrantOfferLetterFinanceTotalsTable, GrantOfferLetterFinanceTotalsTableBuilder> {
    private GrantOfferLetterFinanceTotalsTableBuilder(List<BiConsumer<Integer, GrantOfferLetterFinanceTotalsTable>> multiActions) {
        super(multiActions);
    }

    public static GrantOfferLetterFinanceTotalsTableBuilder newGrantOfferLetterFinanceTotalsTable() {
        return new GrantOfferLetterFinanceTotalsTableBuilder(emptyList());
    }

    @Override
    protected GrantOfferLetterFinanceTotalsTableBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantOfferLetterFinanceTotalsTable>> actions) {
        return new GrantOfferLetterFinanceTotalsTableBuilder(actions);
    }

    @Override
    protected GrantOfferLetterFinanceTotalsTable createInitial() {
        BigDecimal dummyCost = BigDecimal.ZERO;
        String dummyOrgName = "Org";
        Map<String, BigDecimal> dummyCosts = singletonMap(dummyOrgName, dummyCost);
        return new GrantOfferLetterFinanceTotalsTable(
                dummyCosts,
                dummyCosts,
                dummyCosts,
                singletonList(dummyOrgName),
                singletonList(dummyOrgName),
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost
        );
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withIndustrialOrgs(List<String>... industrialOrgs) {
        return withArray((orgs, financeTotalsTable) -> setField("industrialOrgs", orgs, financeTotalsTable), industrialOrgs);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAcademicOrgs(List<String>... academicOrgs) {
        return withArray((orgs, financeTotalsTable) -> setField("academicOrgs", orgs, financeTotalsTable), academicOrgs);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withGrantClaims(Map<String, BigDecimal>... grantClaims) {
        return withArray((claims, financeTotalsTable) -> setField("grantClaims", claims, financeTotalsTable), grantClaims);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withTotalEligibleCosts(Map<String, BigDecimal>... totalEligibleCosts) {
        return withArray((costs, financeTotalsTable) -> setField("totalEligibleCosts", costs, financeTotalsTable), totalEligibleCosts);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withTotalGrant(Map<String, BigDecimal>... totalGrant) {
        return withArray((grant, financeTotalsTable) -> setField("totalGrant", grant, financeTotalsTable), totalGrant);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withIndustryTotalEligibleCosts(BigDecimal... industryTotalEligibleCosts) {
        return withArray((costs, financeTotalsTable) -> setField("industryTotalEligibleCosts", costs, financeTotalsTable), industryTotalEligibleCosts);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAcademicTotalEligibleCosts(BigDecimal... academicTotalEligibleCosts) {
        return withArray((costs, financeTotalsTable) -> setField("academicTotalEligibleCosts", costs, financeTotalsTable), academicTotalEligibleCosts);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAllTotalEligibleCosts(BigDecimal... allTotalEligibleCosts) {
        return withArray((costs, financeTotalsTable) -> setField("allTotalEligibleCosts", costs, financeTotalsTable), allTotalEligibleCosts);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withIndustryTotalGrant(BigDecimal... industryTotalGrant) {
        return withArray((grant, financeTotalsTable) -> setField("industryTotalGrant", grant, financeTotalsTable), industryTotalGrant);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAcademicTotalGrant(BigDecimal... academicTotalGrant) {
        return withArray((grant, financeTotalsTable) -> setField("academicTotalGrant", grant, financeTotalsTable), academicTotalGrant);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAllTotalGrant(BigDecimal... allTotalGrant) {
        return withArray((grant, financeTotalsTable) -> setField("allTotalGrant", grant, financeTotalsTable), allTotalGrant);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withIndustryTotalGrantClaim(BigDecimal... industryTotalGrantClaim) {
        return withArray((claim, financeTotalsTable) -> setField("industryTotalGrantClaim", claim, financeTotalsTable), industryTotalGrantClaim);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAcademicTotalGrantClaim(BigDecimal... academicTotalGrantClaim) {
        return withArray((claim, financeTotalsTable) -> setField("academicTotalGrantClaim", claim, financeTotalsTable), academicTotalGrantClaim);
    }

    public GrantOfferLetterFinanceTotalsTableBuilder withAllTotalGrantClaim(BigDecimal... allTotalGrantClaim) {
        return withArray((claim, financeTotalsTable) -> setField("allTotalGrantClaim", claim, financeTotalsTable), allTotalGrantClaim);
    }

}
