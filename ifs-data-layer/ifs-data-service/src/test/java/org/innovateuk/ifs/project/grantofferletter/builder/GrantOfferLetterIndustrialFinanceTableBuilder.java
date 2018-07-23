package org.innovateuk.ifs.project.grantofferletter.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterIndustrialFinanceTable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for GrantOfferLetterIndustrialFinanceTables.
 */

public class GrantOfferLetterIndustrialFinanceTableBuilder extends BaseBuilder<GrantOfferLetterIndustrialFinanceTable, GrantOfferLetterIndustrialFinanceTableBuilder> {
    private GrantOfferLetterIndustrialFinanceTableBuilder(List<BiConsumer<Integer, GrantOfferLetterIndustrialFinanceTable>> multiActions) {
        super(multiActions);
    }

    public static GrantOfferLetterIndustrialFinanceTableBuilder newGrantOfferLetterIndustrialFinanceTable() {
        return new GrantOfferLetterIndustrialFinanceTableBuilder(emptyList());
    }

    @Override
    protected GrantOfferLetterIndustrialFinanceTableBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantOfferLetterIndustrialFinanceTable>> actions) {
        return new GrantOfferLetterIndustrialFinanceTableBuilder(actions);
    }

    @Override
    protected GrantOfferLetterIndustrialFinanceTable createInitial() {
        String dummyOrgName = "Org";
        BigDecimal dummyCost = BigDecimal.ZERO;
        Map<String, BigDecimal> dummyCosts = singletonMap(dummyOrgName, dummyCost);
        return new GrantOfferLetterIndustrialFinanceTable(
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                singletonList(dummyOrgName)
        );
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withLabour(Map<String, BigDecimal>... labour) {
        return withArray((costs, industrialFinanceTable) -> setField("labour", costs, industrialFinanceTable), labour);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withMaterials(Map<String, BigDecimal>... materials) {
        return withArray((costs, industrialFinanceTable) -> setField("materials", costs, industrialFinanceTable), materials);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withOverheads(Map<String, BigDecimal>... overheads) {
        return withArray((costs, industrialFinanceTable) -> setField("overheads", costs, industrialFinanceTable), overheads);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withCapitalUsage(Map<String, BigDecimal>... capitalUsage) {
        return withArray((costs, industrialFinanceTable) -> setField("capitalUsage", costs, industrialFinanceTable), capitalUsage);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withSubcontract(Map<String, BigDecimal>... subcontract) {
        return withArray((costs, industrialFinanceTable) -> setField("subcontract", costs, industrialFinanceTable), subcontract);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withTravel(Map<String, BigDecimal>... travel) {
        return withArray((costs, industrialFinanceTable) -> setField("travel", costs, industrialFinanceTable), travel);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withOtherCosts(Map<String, BigDecimal>... otherCosts) {
        return withArray((costs, industrialFinanceTable) -> setField("otherCosts", costs, industrialFinanceTable), otherCosts);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withLabourTotal(BigDecimal... labourTotal) {
        return withArray((total, industrialFinanceTable) -> setField("labourTotal", total, industrialFinanceTable), labourTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withMaterialsTotal(BigDecimal... materialsTotal) {
        return withArray((total, industrialFinanceTable) -> setField("materialsTotal", total, industrialFinanceTable), materialsTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withOverheadsTotal(BigDecimal... overheadsTotal) {
        return withArray((total, industrialFinanceTable) -> setField("overheadsTotal", total, industrialFinanceTable), overheadsTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withCapitalUsageTotal(BigDecimal... capitalUsageTotal) {
        return withArray((total, industrialFinanceTable) -> setField("capitalUsageTotal", total, industrialFinanceTable), capitalUsageTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withSubcontractTotal(BigDecimal... subcontractTotal) {
        return withArray((total, industrialFinanceTable) -> setField("subcontractTotal", total, industrialFinanceTable), subcontractTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withTravelTotal(BigDecimal... travelTotal) {
        return withArray((total, industrialFinanceTable) -> setField("travelTotal", total, industrialFinanceTable), travelTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withOtherCostsTotal(BigDecimal... otherCostsTotal) {
        return withArray((total, industrialFinanceTable) -> setField("otherCostsTotal", total, industrialFinanceTable), otherCostsTotal);
    }

    public GrantOfferLetterIndustrialFinanceTableBuilder withOrganisations(List<String>... organisations) {
        return withArray((orgs, industrialFinanceTable) -> setField("organisations", orgs, industrialFinanceTable), organisations);
    }

}
