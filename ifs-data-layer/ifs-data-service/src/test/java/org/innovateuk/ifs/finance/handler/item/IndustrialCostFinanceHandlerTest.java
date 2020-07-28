package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.CapitalUsageBuilder.newCapitalUsage;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.SubcontractingCostBuilder.newSubContractingCost;
import static org.innovateuk.ifs.finance.builder.VATCostBuilder.newVATCost;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class IndustrialCostFinanceHandlerTest {
    @InjectMocks
    private IndustrialCostFinanceHandler industrialCostFinanceHandler;
    @Mock
    private ApplicationFinanceRowRepository financeRowRepositoryMock;
    @Mock
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;
    @Spy
    private LabourCostHandler labourCostHandler;
    @Spy
    private CapitalUsageHandler capitalUsageHandler;
    @Spy
    private MaterialsHandler materialsHandler;
    @Spy
    private OtherCostHandler otherCostHandler;
    @Spy
    private OverheadsHandler overheadsHandler;
    @Spy
    private SubContractingCostHandler subContractingCostHandler;
    @Spy
    private TravelCostHandler travelCostHandler;
    @Spy
    private GrantClaimPercentageHandler grantClaimHandler;
    @Spy
    private GrantClaimAmountHandler grantClaimAmountHandler;
    @Spy
    private OtherFundingHandler otherFundingHandler;
    @Spy
    private VatHandler vatHandler;
    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;
    private ApplicationFinance applicationFinance;
    private Materials material;
    private FinanceRow materialCost;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        industrialCostFinanceHandler.setFinanceRowHandlers(asList(labourCostHandler, capitalUsageHandler, materialsHandler, otherCostHandler,
                overheadsHandler, subContractingCostHandler, travelCostHandler, grantClaimAmountHandler, grantClaimHandler,
                otherFundingHandler, vatHandler));

        when(financeRowRepositoryMock.saveAll(anyList())).then(returnsFirstArg());

        Competition competition = newCompetition()
                .withFundingType(FundingType.GRANT)
                .withCompetitionType(newCompetitionType().withName("Horizon 2020").build())
                .withFinanceRowTypes(Arrays.stream(FinanceRowType.values()).collect(Collectors.toList()))
                .build();

        Application application = newApplication().withCompetition(competition).build();
        applicationFinance = newApplicationFinance().withApplication(application).build();

        List<ApplicationFinanceRow> costs = new ArrayList<>();

        Iterable<ApplicationFinanceRow> init;
        for (FinanceRowType costType : values()) {
            init = industrialCostFinanceHandler.initialiseCostType(applicationFinance, costType);
            if (init != null) {
                init.forEach(costs::add);
            }
        }

        CapitalUsage capitalUsage = newCapitalUsage()
                .withId((Long) null)
                .withDeprecation(20)
                .withDescription("Description")
                .withExisting("Yes")
                .withNpv(BigDecimal.valueOf(100000))
                .withResidualValue(BigDecimal.valueOf(200000))
                .withUtilisation(20)
                .withTargetId(applicationFinance.getId())
                .build();

        FinanceRowMetaField financeRowMetaField = newFinanceRowMetaField()
                .withId(3L)
                .withTitle("existing")
                .withType("String")
                .build();

        FinanceRowMetaField financeRowMetaField2 = newFinanceRowMetaField()
                .withId(4L)
                .withTitle("residual_value")
                .withType("BigDecimal")
                .build();

        FinanceRowMetaField financeRowMetaField3 = newFinanceRowMetaField()
                .withId(5L)
                .withTitle("utilisation")
                .withType("Integer")
                .build();

        FinanceRowMetaValue financeRowMetaValue1 = newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField, financeRowMetaField2)
                .withValue("Yes", String.valueOf(new BigDecimal(100000)))
                .build();

        FinanceRowMetaValue financeRowMetaValue2 = newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField3, null)
                .withValue(String.valueOf(20), String.valueOf(20))
                .build();

        FinanceRow capitalUsageCost = industrialCostFinanceHandler.toApplicationDomain(capitalUsage);
        capitalUsageCost.setTarget(applicationFinance);
        capitalUsageCost.getFinanceRowMetadata().add(financeRowMetaValue1);
        capitalUsageCost.getFinanceRowMetadata().add(financeRowMetaValue2);
        costs.add((ApplicationFinanceRow) capitalUsageCost);

        SubContractingCost subContracting = newSubContractingCost()
                .withId((Long) null)
                .withCost(BigDecimal.ONE)
                .withCountry("France")
                .withName("Name")
                .withRole("Role")
                .withTargetId(applicationFinance.getId())
                .build();

        FinanceRowMetaField financeRowMetaField4 = newFinanceRowMetaField()
                .withId(1L)
                .withTitle("country")
                .withType("france")
                .build();

        FinanceRowMetaValue financeRowMetaValue3 = newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField4)
                .withValue("frane")
                .build();

        FinanceRow subContractingCost = industrialCostFinanceHandler.toApplicationDomain(subContracting);
        subContractingCost.getFinanceRowMetadata().add(financeRowMetaValue3);
        subContractingCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow) subContractingCost);

        SubContractingCost subContracting2 = newSubContractingCost()
                .withId((Long) null)
                .withCost(BigDecimal.TEN)
                .withCountry("France")
                .withName("Name")
                .withRole("Role")
                .withTargetId(applicationFinance.getId())
                .build();

        FinanceRowMetaField financeRowMetaField5 = newFinanceRowMetaField()
                .withId(2L)
                .withTitle("country")
                .withType("france")
                .build();

        FinanceRowMetaValue financeRowMetaValue4 = newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField5)
                .withValue("frane")
                .build();

        FinanceRow subContractingCost2 = industrialCostFinanceHandler.toApplicationDomain(subContracting2);
        subContractingCost2.getFinanceRowMetadata().add(financeRowMetaValue4);
        subContractingCost2.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow) subContractingCost2);

        LabourCost labour = newLabourCost()
                .withLabourDays(300)
                .withGrossEmployeeCost(BigDecimal.valueOf(50000))
                .withRole("Developer")
                .withDescription("")
                .build();

        FinanceRow labourCost = industrialCostFinanceHandler.toApplicationDomain(labour);
        labourCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow) labourCost);

        material = newMaterials()
                .withCost(BigDecimal.valueOf(100))
                .withItem("Screws")
                .withQuantity(5)
                .withTargetId(applicationFinance.getId())
                .build();

        materialCost = industrialCostFinanceHandler.toApplicationDomain(material);
        materialCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow) materialCost);

        Vat vat = newVATCost()
                .withRegistered(false)
                .withTargetId(applicationFinance.getId())
                .build();

        FinanceRow vatCost = industrialCostFinanceHandler.toApplicationDomain(vat);
        vatCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow) vatCost);

        when(applicationFinanceRepository.findById(any())).thenReturn(Optional.ofNullable(applicationFinance));
        when(financeRowRepositoryMock.findByTargetId(applicationFinance.getId())).thenReturn(costs);
        when(financeRowMetaFieldRepository.findAll()).thenReturn(new ArrayList<>());
    }

    @Test
    public void getOrganisationFinancesMaterials() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());

        assertEquals("Testing equality for: " + MATERIALS.getType(),
                new BigDecimal(500), organisationFinances.get(MATERIALS).getTotal());
    }

    @Test
    public void getOrganisationFinancesOtherCosts() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; " + OTHER_COSTS.getType(), new BigDecimal(0), organisationFinances.get(OTHER_COSTS).getTotal());
    }

    @Test
    public void getOrganisationFinancesVat() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; " + VAT.getType(), new BigDecimal(0), organisationFinances.get(VAT).getTotal());
    }

    @Test
    public void getOrganisationFinancesCapitalUsage() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; " + CAPITAL_USAGE.getType(), new BigDecimal(20000).setScale(2), organisationFinances.get(CAPITAL_USAGE).getTotal().setScale(2));
    }

    @Test
    public void getOrganisationFinancesSubcontractingCost() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; " + SUBCONTRACTING_COSTS.getType(), new BigDecimal(11).setScale(2), organisationFinances.get(SUBCONTRACTING_COSTS).getTotal().setScale(2));
    }

    @Test
    public void getOrganisationFinancesLabour() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = industrialCostFinanceHandler.getOrganisationFinances(applicationFinance.getId());
        LabourCostCategory labourCategory = (LabourCostCategory) organisationFinances.get(LABOUR);
        labourCategory.getWorkingDaysPerYearCostItem().setLabourDays(25);
        labourCategory.calculateTotal();
        assertEquals(0, new BigDecimal(600000).compareTo(labourCategory.getTotal()));
        assertEquals("Testing equality for; " + LABOUR.getType(), new BigDecimal(600000).setScale(5),
                organisationFinances.get(LABOUR).getTotal().setScale(5));
    }

    @Test
    public void costItemToCost() {
        FinanceRow materialCostTmp = industrialCostFinanceHandler.toApplicationDomain(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void costItemToProjectCost() {
        FinanceRow materialCostTmp = industrialCostFinanceHandler.toProjectDomain(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void getHandlerMatches() {
        asList(Pair.of(MATERIALS, MaterialsHandler.class),
                Pair.of(LABOUR, LabourCostHandler.class),
                Pair.of(TRAVEL, TravelCostHandler.class),
                Pair.of(CAPITAL_USAGE, CapitalUsageHandler.class),
                Pair.of(SUBCONTRACTING_COSTS, SubContractingCostHandler.class),
                Pair.of(OVERHEADS, OverheadsHandler.class),
                Pair.of(OTHER_COSTS, OtherCostHandler.class),
                Pair.of(OTHER_FUNDING, OtherFundingHandler.class),
                Pair.of(VAT, VatHandler.class)
        ).forEach(pair -> {
            final FinanceRowType costType = pair.getKey();
            final Class<?> clazz = pair.getValue();
            assertTrue("Correct handler for " + costType, clazz.isAssignableFrom(industrialCostFinanceHandler.getCostHandler(costType).getClass()));
        });
    }

    @Test
    public void costToCostItem() {
        final FinanceRowItem costItem = industrialCostFinanceHandler.toResource(materialCost);
        assertEquals(costItem.getTotal(), materialCost.getCost().multiply(new BigDecimal(materialCost.getQuantity())));
        assertEquals(costItem.getName(), materialCost.getName());
        assertEquals(costItem.getId(), materialCost.getId());
    }
}
