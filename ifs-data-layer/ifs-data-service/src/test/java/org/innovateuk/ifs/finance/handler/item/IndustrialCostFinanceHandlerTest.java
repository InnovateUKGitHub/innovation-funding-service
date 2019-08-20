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
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;
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

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
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
    private IndustrialCostFinanceHandler handler;
    @Mock
    private ApplicationFinanceRowRepository financeRowRepositoryMock;
    @Mock
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;
    @Mock
    private QuestionService questionService;
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
    private GrantClaimHandler grantClaimHandler;
    @Spy
    private OtherFundingHandler otherFundingHandler;
    @Spy
    private VatHandler vatHandler;
    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;
    private Competition competition;
    private Application application;
    private ApplicationFinance applicationFinance;
    private HashMap<FinanceRowType, Question> costTypeQuestion;
    private LabourCost labour;
    private CapitalUsage capitalUsage;
    private FinanceRow capitalUsageCost;
    private SubContractingCost subContracting;
    private FinanceRow subContractingCost;
    private FinanceRow labourCost;
    private Materials material;
    private Vat vat;
    private FinanceRow vatCost;
    private FinanceRow materialCost;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(financeRowRepositoryMock.saveAll(anyList())).then(returnsFirstArg());

        competition = newCompetition()
                .withFundingType(FundingType.GRANT)
                .withCompetitionType(newCompetitionType().withName("Horizon 2020").build())
                .withFinanceRowTypes(EnumSet.allOf(FinanceRowType.class))
                .build();
        application = newApplication().withCompetition(competition).build();
        applicationFinance = newApplicationFinance().withApplication(application).build();
        costTypeQuestion = new HashMap<>();

        List<ApplicationFinanceRow> costs = new ArrayList<>();

        Iterable<ApplicationFinanceRow> init;
        for (FinanceRowType costType : FinanceRowType.values()) {
            init = handler.initialiseCostType(applicationFinance, costType);
            if(init != null){
                init.forEach(i -> costs.add(i));
            }
        }

        capitalUsage =  new CapitalUsage(null, 20,"Description", "Yes", new BigDecimal(200000), new BigDecimal(100000), 20, applicationFinance.getId());
        capitalUsageCost = handler.toApplicationDomain(capitalUsage);
        capitalUsageCost.setTarget(applicationFinance);
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(3l, "existing", "String"), "Yes"));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(4l, "residual_value", "BigDecimal"), String.valueOf(new BigDecimal(100000))));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(5l, "utilisation", "Integer"), String.valueOf(20)));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(6L, null, "Integer"), String.valueOf(20)));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, null, String.valueOf(20)));
        costs.add((ApplicationFinanceRow) capitalUsageCost);

        subContracting = new SubContractingCost(null, BigDecimal.ONE, "france", "name", "role", applicationFinance.getId());
        subContractingCost = handler.toApplicationDomain(subContracting);
        subContractingCost.setTarget(applicationFinance);
        subContractingCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(new FinanceRowMetaField(1l, "country", "france"), "frane"));
        costs.add((ApplicationFinanceRow)subContractingCost);
        SubContractingCost subContracting2 = new SubContractingCost(null, BigDecimal.TEN, "france", "name", "role", applicationFinance.getId());
        FinanceRow subContractingCost2 = handler.toApplicationDomain(subContracting2);
        subContractingCost2.getFinanceRowMetadata().add(new FinanceRowMetaValue(new FinanceRowMetaField(2l, "country", "france"), "frane"));
        subContractingCost2.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow)subContractingCost2);

        labour = new LabourCost(applicationFinance.getId());
        labour.setLabourDays(300);
        labour.setGrossEmployeeCost(BigDecimal.valueOf(50000));
        labour.setRole("Developer");
        labour.setDescription("");
        labourCost = handler.toApplicationDomain(labour);
        labourCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow)labourCost);

        material = new Materials(applicationFinance.getId());
        material.setCost(BigDecimal.valueOf(100));
        material.setItem("Screws");
        material.setQuantity(5);
        materialCost = handler.toApplicationDomain(material);
        materialCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow)materialCost);

        vat = new Vat(applicationFinance.getId());
        vat.setRegistered(false);
        vatCost = handler.toApplicationDomain(vat);
        vatCost.setTarget(applicationFinance);
        costs.add((ApplicationFinanceRow)vatCost);

        when(applicationFinanceRepository.findById(any())).thenReturn(Optional.ofNullable(applicationFinance));
        when(financeRowRepositoryMock.findByTargetId(applicationFinance.getId())).thenReturn(costs);
        when(financeRowMetaFieldRepository.findAll()).thenReturn(new ArrayList<>());
    }

    @Test
    public void getOrganisationFinancesMaterials() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());

        assertEquals("Testing equality for: " + FinanceRowType.MATERIALS.getType(),
                new BigDecimal(500), organisationFinances.get(FinanceRowType.MATERIALS).getTotal());
    }

    @Test
    public void getOrganisationFinancesOtherCosts() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ FinanceRowType.OTHER_COSTS.getType(), new BigDecimal(0), organisationFinances.get(FinanceRowType.OTHER_COSTS).getTotal());
    }

    @Test
    public void getOrganisationFinancesVat() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ FinanceRowType.VAT.getType(), new BigDecimal(0), organisationFinances.get(FinanceRowType.VAT).getTotal());
    }

    @Test
    public void getOrganisationFinancesCapitalUsage() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ FinanceRowType.CAPITAL_USAGE.getType(), new BigDecimal(20000).setScale(2), organisationFinances.get(FinanceRowType.CAPITAL_USAGE).getTotal().setScale(2));
    }
    @Test
    public void getOrganisationFinancesSubcontractingCost() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ FinanceRowType.SUBCONTRACTING_COSTS.getType(), new BigDecimal(11).setScale(2), organisationFinances.get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().setScale(2));
    }

    @Test
    public void getOrganisationFinancesLabour() {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        LabourCostCategory labourCategory = (LabourCostCategory) organisationFinances.get(FinanceRowType.LABOUR);
        labourCategory.getWorkingDaysPerYearCostItem().setLabourDays(25);
        labourCategory.calculateTotal();
        assertEquals(0, new BigDecimal(600000).compareTo(labourCategory.getTotal()));
        assertEquals("Testing equality for; "+ FinanceRowType.LABOUR.getType(), new BigDecimal(600000).setScale(5),
                organisationFinances.get(FinanceRowType.LABOUR).getTotal().setScale(5));
    }

    @Test
    public void costItemToCost() {
        FinanceRow materialCostTmp = handler.toApplicationDomain(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void costItemToProjectCost() {
        FinanceRow materialCostTmp = handler.toProjectDomain(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void getHandlerMatches() {
        asList( Pair.of(MATERIALS, MaterialsHandler.class),
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
            assertTrue("Correct handler for " + costType, clazz.isAssignableFrom(handler.getCostHandler(costType).getClass()));
        });
    }

    @Test
    public void costToCostItem() {
        final FinanceRowItem costItem = handler.toResource(materialCost);
        assertEquals(costItem.getTotal(), materialCost.getCost().multiply(new BigDecimal(materialCost.getQuantity())));
        assertEquals(costItem.getName(), materialCost.getName());
        assertEquals(costItem.getId(), materialCost.getId());
    }
}
