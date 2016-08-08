package com.worth.ifs.finance.handler.item;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.FinanceRowMetaValue;
import com.worth.ifs.finance.handler.OrganisationFinanceDefaultHandler;
import com.worth.ifs.finance.handler.OrganisationFinanceHandler;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.*;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.math.BigDecimal;
import java.util.*;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class OrganisationFinanceHandlerTest {
    @InjectMocks
    OrganisationFinanceHandler handler = new OrganisationFinanceDefaultHandler();
    @Mock
    AutowireCapableBeanFactory beanFactory;
    @Mock
    CostRepository costRepositoryMock;
    @Mock
    CostFieldRepository costFieldRepository;
    @Mock
    QuestionService questionService;
    private Competition competition;
    private Application application;
    private ApplicationFinance applicationFinance;
    private HashMap<CostType, Question> costTypeQuestion;
    private LabourCost labour;
    private CapitalUsage capitalUsage;
    private Cost capitalUsageCost;
    private SubContractingCost subContracting;
    private Cost subContractingCost;
    private Cost labourCost;
    private Materials material;
    private Cost materialCost;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        competition = newCompetition().build();
        application = newApplication().withCompetition(competition).build();
        applicationFinance = newApplicationFinance().withApplication(application).build();
        costTypeQuestion = new HashMap<CostType, Question>();

        for (CostType costType : CostType.values()) {
            setUpCostTypeQuestions(costType);
        }

        List<Cost> costs = new ArrayList<>();

        Iterable<Cost> init;
        for (CostType costType : CostType.values()) {
            init = handler.initialiseCostType(applicationFinance, costType);
            if(init != null){
                init.forEach(i -> costs.add(i));
            }
        }


        capitalUsage =  new CapitalUsage(null, 20,"Description", "Yes", new BigDecimal(200000), new BigDecimal(100000), 20);
        capitalUsageCost = handler.costItemToCost(capitalUsage);
        capitalUsageCost.getCostValues().add(new FinanceRowMetaValue(capitalUsageCost, new CostField(3l, "existing", "String"), "Yes"));
        capitalUsageCost.getCostValues().add(new FinanceRowMetaValue(capitalUsageCost, new CostField(4l, "residual_value", "BigDecimal"), String.valueOf(new BigDecimal(100000))));
        capitalUsageCost.getCostValues().add(new FinanceRowMetaValue(capitalUsageCost, new CostField(5l, "utilisation", "Integer"), String.valueOf(20)));
        capitalUsageCost.getCostValues().add(new FinanceRowMetaValue(capitalUsageCost, new CostField(6L, null, "Integer"), String.valueOf(20)));
        capitalUsageCost.getCostValues().add(new FinanceRowMetaValue(capitalUsageCost, null, String.valueOf(20)));
        capitalUsageCost.setQuestion(costTypeQuestion.get(CostType.CAPITAL_USAGE));
        costs.add(capitalUsageCost);

        subContracting = new SubContractingCost(null, BigDecimal.ONE, "france", "name", "role");
        subContractingCost = handler.costItemToCost(subContracting);
        subContractingCost.getCostValues().add(new FinanceRowMetaValue(new CostField(1l, "country", "france"), "frane"));
        subContractingCost.setQuestion(costTypeQuestion.get(CostType.SUBCONTRACTING_COSTS));
        costs.add(subContractingCost);
        SubContractingCost subContracting2 = new SubContractingCost(null, BigDecimal.TEN, "france", "name", "role");
        Cost subContractingCost2 = handler.costItemToCost(subContracting2);
        subContractingCost2.getCostValues().add(new FinanceRowMetaValue(new CostField(2l, "country", "france"), "frane"));
        subContractingCost2.setQuestion(costTypeQuestion.get(CostType.SUBCONTRACTING_COSTS));
        costs.add(subContractingCost2);

        labour = new LabourCost();
        labour.setLabourDays(300);
        labour.setGrossAnnualSalary(BigDecimal.valueOf(50000));
        labour.setRole("Developer");
        labour.setDescription("");
        labourCost = handler.costItemToCost(labour);
        labourCost.setQuestion(costTypeQuestion.get(CostType.LABOUR));
        costs.add(labourCost);

        material = new Materials();
        material.setCost(BigDecimal.valueOf(100));
        material.setItem("Screws");
        material.setQuantity(5);
        materialCost = handler.costItemToCost(material);
        materialCost.setQuestion(costTypeQuestion.get(CostType.MATERIALS));
        costs.add(materialCost);

        when(costRepositoryMock.findByApplicationFinanceId(applicationFinance.getId())).thenReturn(costs);
        when(costFieldRepository.findAll()).thenReturn(new ArrayList<CostField>());

    }

    private void setUpCostTypeQuestions(CostType costType) {
        FormInputType formInputType = new FormInputType(null, costType.getType());
        FormInput formInput = newFormInput().build();
        formInput.setFormInputType(formInputType);
        Question question = newQuestion().withFormInputs(Arrays.asList(formInput)).build();

        costTypeQuestion.put(costType, question);
        when(questionService.getQuestionByFormInputType(eq(costType.getType()))).thenReturn(ServiceResult.serviceSuccess(question));
    }

    @Test
    public void testGetOrganisationFinancesMaterials() throws Exception {
        Map<CostType, CostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());

        assertEquals("Testing equality for; " + CostType.MATERIALS.getType(), new BigDecimal(500), organisationFinances.get(CostType.MATERIALS).getTotal());
    }

    @Test
    public void testGetOrganisationFinancesOtherCosts() throws Exception {
        Map<CostType, CostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ CostType.OTHER_COSTS.getType(), new BigDecimal(0), organisationFinances.get(CostType.OTHER_COSTS).getTotal());
    }

    @Test
    public void testGetOrganisationFinancesCapitalUsage() throws Exception {
        Map<CostType, CostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ CostType.CAPITAL_USAGE.getType(), new BigDecimal(20000).setScale(2), organisationFinances.get(CostType.CAPITAL_USAGE).getTotal().setScale(2));
    }
    @Test
    public void testGetOrganisationFinancesSubcontractingCost() throws Exception {
        Map<CostType, CostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        assertEquals("Testing equality for; "+ CostType.SUBCONTRACTING_COSTS.getType(), new BigDecimal(11).setScale(2), organisationFinances.get(CostType.SUBCONTRACTING_COSTS).getTotal().setScale(2));
    }

    @Test
    public void testGetOrganisationFinancesLabour() throws Exception {
        Map<CostType, CostCategory> organisationFinances = handler.getOrganisationFinances(applicationFinance.getId());
        LabourCostCategory labourCategory = (LabourCostCategory) organisationFinances.get(CostType.LABOUR);
        labourCategory.getWorkingDaysPerYearCostItem().setLabourDays(25);
        labourCategory.calculateTotal();
        assertEquals(0, new BigDecimal(600000).compareTo(labourCategory.getTotal()));
        assertEquals("Testing equality for; "+ CostType.LABOUR.getType(), new BigDecimal(600000).setScale(5), organisationFinances.get(CostType.LABOUR).getTotal().setScale(5));
    }

    @Test
    public void testGetOrganisationFinanceTotals() throws Exception {

    }

    @Test
    public void testCostItemToCost() throws Exception {
        Cost materialCostTmp = handler.costItemToCost(material);
        assertEquals(new BigDecimal(100), materialCostTmp.getCost());
        assertEquals("", materialCostTmp.getDescription());
        assertEquals("Screws", materialCostTmp.getItem());
        assertEquals(Integer.valueOf(5), materialCostTmp.getQuantity());

    }

    @Test
    public void testCostToCostItem() throws Exception {

    }

    @Test
    public void testCostItemsToCost() throws Exception {

    }
}