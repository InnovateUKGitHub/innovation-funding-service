package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.*;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDefaultHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationFinanceHandlerTest {
    @InjectMocks
    private OrganisationFinanceDefaultHandler handler;
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
    private FinanceRow materialCost;

    @Before
    public void setUp() throws Exception {
        when(financeRowRepositoryMock.save(anyList())).then(returnsFirstArg());

        competition = newCompetition().withFundingType(FundingType.GRANT).build();
        application = newApplication().withCompetition(competition).build();
        applicationFinance = newApplicationFinance().withApplication(application).build();
        costTypeQuestion = new HashMap<>();

        for (FinanceRowType costType : FinanceRowType.values()) {
            if (ACADEMIC != costType) {
                setUpCostTypeQuestions(competition, costType);
            }
        }
        List<ApplicationFinanceRow> costs = new ArrayList<>();

        Iterable<ApplicationFinanceRow> init;
        for (FinanceRowType costType : FinanceRowType.values()) {
            init = handler.initialiseCostType(applicationFinance, costType);
            if(init != null){
                init.forEach(i -> costs.add(i));
            }
        }


        capitalUsage =  new CapitalUsage(null, 20,"Description", "Yes", new BigDecimal(200000), new BigDecimal(100000), 20);
        capitalUsageCost = handler.costItemToCost(capitalUsage);
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(3l, "existing", "String"), "Yes"));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(4l, "residual_value", "BigDecimal"), String.valueOf(new BigDecimal(100000))));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(5l, "utilisation", "Integer"), String.valueOf(20)));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, new FinanceRowMetaField(6L, null, "Integer"), String.valueOf(20)));
        capitalUsageCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(capitalUsageCost, null, String.valueOf(20)));
        capitalUsageCost.setQuestion(costTypeQuestion.get(FinanceRowType.CAPITAL_USAGE));
        costs.add((ApplicationFinanceRow) capitalUsageCost);

        subContracting = new SubContractingCost(null, BigDecimal.ONE, "france", "name", "role");
        subContractingCost = handler.costItemToCost(subContracting);
        subContractingCost.getFinanceRowMetadata().add(new FinanceRowMetaValue(new FinanceRowMetaField(1l, "country", "france"), "frane"));
        subContractingCost.setQuestion(costTypeQuestion.get(FinanceRowType.SUBCONTRACTING_COSTS));
        costs.add((ApplicationFinanceRow)subContractingCost);
        SubContractingCost subContracting2 = new SubContractingCost(null, BigDecimal.TEN, "france", "name", "role");
        FinanceRow subContractingCost2 = handler.costItemToCost(subContracting2);
        subContractingCost2.getFinanceRowMetadata().add(new FinanceRowMetaValue(new FinanceRowMetaField(2l, "country", "france"), "frane"));
        subContractingCost2.setQuestion(costTypeQuestion.get(FinanceRowType.SUBCONTRACTING_COSTS));
        costs.add((ApplicationFinanceRow)subContractingCost2);

        labour = new LabourCost();
        labour.setLabourDays(300);
        labour.setGrossEmployeeCost(BigDecimal.valueOf(50000));
        labour.setRole("Developer");
        labour.setDescription("");
        labourCost = handler.costItemToCost(labour);
        labourCost.setQuestion(costTypeQuestion.get(FinanceRowType.LABOUR));
        costs.add((ApplicationFinanceRow)labourCost);

        material = new Materials();
        material.setCost(BigDecimal.valueOf(100));
        material.setItem("Screws");
        material.setQuantity(5);
        materialCost = handler.costItemToCost(material);
        materialCost.setQuestion(costTypeQuestion.get(FinanceRowType.MATERIALS));
        costs.add((ApplicationFinanceRow)materialCost);

        when(financeRowRepositoryMock.findByTargetId(applicationFinance.getId())).thenReturn(costs);
        when(financeRowMetaFieldRepository.findAll()).thenReturn(new ArrayList<>());
    }

    private void setUpCostTypeQuestions(Competition competition, FinanceRowType costType) {
        FormInput formInput = newFormInput()
                .withType(costType.getFormInputType())
                .build();
        Question question = newQuestion().withFormInputs(asList(formInput)).build();

        costTypeQuestion.put(costType, question);
        when(questionService.getQuestionByCompetitionIdAndFormInputType(eq(competition.getId()), eq(costType.getFormInputType())))
                .thenReturn(serviceSuccess(question));
    }

    @Test
    public void getOrganisationFinancesMaterials() throws Exception {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId(), competition);

        assertEquals("Testing equality for: " + FinanceRowType.MATERIALS.getType(),
                new BigDecimal(500), organisationFinances.get(FinanceRowType.MATERIALS).getTotal());
    }

    @Test
    public void getOrganisationFinancesOtherCosts() throws Exception {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId(), competition);
        assertEquals("Testing equality for; "+ FinanceRowType.OTHER_COSTS.getType(), new BigDecimal(0), organisationFinances.get(FinanceRowType.OTHER_COSTS).getTotal());
    }

    @Test
    public void getOrganisationFinancesCapitalUsage() throws Exception {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId(), competition);
        assertEquals("Testing equality for; "+ FinanceRowType.CAPITAL_USAGE.getType(), new BigDecimal(20000).setScale(2), organisationFinances.get(FinanceRowType.CAPITAL_USAGE).getTotal().setScale(2));
    }
    @Test
    public void getOrganisationFinancesSubcontractingCost() throws Exception {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId(), competition);
        assertEquals("Testing equality for; "+ FinanceRowType.SUBCONTRACTING_COSTS.getType(), new BigDecimal(11).setScale(2), organisationFinances.get(FinanceRowType.SUBCONTRACTING_COSTS).getTotal().setScale(2));
    }

    @Test
    public void getOrganisationFinancesLabour() throws Exception {
        Map<FinanceRowType, FinanceRowCostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId(), competition);
        LabourCostCategory labourCategory = (LabourCostCategory) organisationFinances.get(FinanceRowType.LABOUR);
        labourCategory.getWorkingDaysPerYearCostItem().setLabourDays(25);
        labourCategory.calculateTotal();
        assertEquals(0, new BigDecimal(600000).compareTo(labourCategory.getTotal()));
        assertEquals("Testing equality for; "+ FinanceRowType.LABOUR.getType(), new BigDecimal(600000).setScale(5),
                organisationFinances.get(FinanceRowType.LABOUR).getTotal().setScale(5));
    }

    @Test
    public void costItemToCost() throws Exception {
        FinanceRow materialCostTmp = handler.costItemToCost(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void costItemToProjectCost() throws Exception {
        FinanceRow materialCostTmp = handler.costItemToProjectCost(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());
    }

    @Test
    public void getHandlerMatches() throws Exception {
        asList( Pair.of(MATERIALS, MaterialsHandler.class),
                Pair.of(LABOUR, LabourCostHandler.class),
                Pair.of(TRAVEL, TravelCostHandler.class),
                Pair.of(CAPITAL_USAGE, CapitalUsageHandler.class),
                Pair.of(SUBCONTRACTING_COSTS, SubContractingCostHandler.class),
                Pair.of(OVERHEADS, OverheadsHandler.class),
                Pair.of(OTHER_COSTS, OtherCostHandler.class),
                Pair.of(OTHER_FUNDING, OtherFundingHandler.class)
        ).forEach(pair -> {
            final FinanceRowType costType = pair.getKey();
            final Class<?> clazz = pair.getValue();
            assertTrue("Correct handler for " + costType, clazz.isAssignableFrom(handler.getCostHandler(costType).getClass()));
        });
    }

    @Test
    public void costToCostItem() throws Exception {
        final FinanceRowItem costItem = handler.costToCostItem((ApplicationFinanceRow) materialCost);
        assertEquals(costItem.getTotal(), materialCost.getCost().multiply(new BigDecimal(materialCost.getQuantity())));
        assertEquals(costItem.getName(), materialCost.getName());
        assertEquals(costItem.getId(), materialCost.getId());
    }

    @Test
    public void getProjectOrganisationFinanceChanges() throws Exception {
    //TODO
    }

}
