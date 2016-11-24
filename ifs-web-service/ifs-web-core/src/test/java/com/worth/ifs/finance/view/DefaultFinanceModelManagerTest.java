package com.worth.ifs.finance.view;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.DefaultFinanceModelManager;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.LabourCost;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.MapFunctions;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFinanceModelManagerTest {

	@InjectMocks
	private DefaultFinanceModelManager manager;
	
    @Mock
    private QuestionService questionService;
    @Mock
    private FinanceService financeService;
    @Mock
    private OrganisationTypeRestService organisationTypeService;
    @Mock
    private FinanceHandler financeHandler;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private FormInputService formInputService;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private CompetitionService competitionService;
	
    private Model model;
    private Long applicationId;
    private List<QuestionResource> costsQuestions;
    private Long userId;
    private Form form;
    private Long competitionId;
    private Long organisationId;
    
    @Before
    public void setUp() {
		model = new ExtendedModelMap();
		applicationId = 1L;
		costsQuestions = newQuestionResource().build(1);
		userId = 2L;
		form = new Form();
		competitionId = 3L;
		organisationId = 4L;
    }
    
	@Test
	public void testAddOrganisationFinanceDetailsClosedCompetitionNotSubmittedApplication() {

		ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();
		CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();
		
		FinanceFormHandler financeFormHandler = mock(FinanceFormHandler.class);
		
		setupStubs(applicationId, costsQuestions, userId, competitionId, organisationId, application, competition,
				financeFormHandler);
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form);
		
		assertEquals(6, model.asMap().size());
		verify(financeFormHandler, never()).addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId());
	}
	
	@Test
	public void testAddOrganisationFinanceDetailsOpenCompetitionSubmittedApplication() {

		ApplicationResource application = newApplicationResource().withCompetition(competitionId).withApplicationStatus(ApplicationStatusConstants.SUBMITTED).build();
		CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
		
		FinanceFormHandler financeFormHandler = mock(FinanceFormHandler.class);
		
		setupStubs(applicationId, costsQuestions, userId, competitionId, organisationId, application, competition,
				financeFormHandler);
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form);
		
		assertEquals(6, model.asMap().size());
		verify(financeFormHandler, never()).addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId());
	}
	
	@Test
	public void testAddOrganisationFinanceDetailsOpenCompetitionNotSubmittedApplication() {
		
		ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();
		CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
		
		FinanceFormHandler financeFormHandler = mock(FinanceFormHandler.class);
		
		setupStubs(applicationId, costsQuestions, userId, competitionId, organisationId, application, competition,
				financeFormHandler);
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form);
		
		assertEquals(6, model.asMap().size());
		verify(financeFormHandler).addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId());
	}

	private void setupStubs(Long applicationId, List<QuestionResource> costsQuestions, Long userId, Long competitionId,
			Long organisationId, ApplicationResource application, CompetitionResource competition,
			FinanceFormHandler financeFormHandler) {
		when(applicationService.getById(applicationId)).thenReturn(application);
		when(competitionService.getById(competitionId)).thenReturn(competition);
	
		ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().withOrganisation(organisationId).build();
		Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = MapFunctions.asMap(FinanceRowType.LABOUR, new LabourCostCategory());
		applicationFinance.setFinanceOrganisationDetails(financeOrganisationDetails);
		when(financeService.getApplicationFinanceDetails(userId, applicationId)).thenReturn(applicationFinance);
		OrganisationTypeResource organisationTypeResource = newOrganisationTypeResource().build();
		when(organisationTypeService.getForOrganisationId(organisationId)).thenReturn(restSuccess(organisationTypeResource));
		
		String organisationType = "orgtype";
		when(organisationService.getOrganisationType(userId, applicationId)).thenReturn(organisationType);
		
		when(financeHandler.getFinanceFormHandler(organisationType)).thenReturn(financeFormHandler);
		
    	when(formInputService.findApplicationInputsByQuestion(isA(Long.class))).thenReturn(asList(newFormInputResource().withFormInputTypeTitle("labour").build()));

		FinanceRowItem costItem = new LabourCost();
		when(financeFormHandler.addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId())).thenReturn(costItem);
	}
}
