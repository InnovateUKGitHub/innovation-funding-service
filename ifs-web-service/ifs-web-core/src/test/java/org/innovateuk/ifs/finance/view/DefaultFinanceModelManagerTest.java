package org.innovateuk.ifs.finance.view;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

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
	@Mock
	private OrganisationSizeService organisationSizeService;
	
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
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form, organisationId);
		
		assertEquals(9, model.asMap().size());
		verify(financeFormHandler, never()).addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId());
	}
	
	@Test
	public void testAddOrganisationFinanceDetailsOpenCompetitionSubmittedApplication() {

		ApplicationResource application = newApplicationResource().withCompetition(competitionId).withApplicationStatus(ApplicationStatus.SUBMITTED).build();
		CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
		
		FinanceFormHandler financeFormHandler = mock(FinanceFormHandler.class);
		
		setupStubs(applicationId, costsQuestions, userId, competitionId, organisationId, application, competition,
				financeFormHandler);
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form, organisationId);
		
		assertEquals(9, model.asMap().size());
		verify(financeFormHandler, never()).addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId());
	}
	
	@Test
	public void testAddOrganisationFinanceDetailsOpenCompetitionNotSubmittedApplication() {
		
		ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();
		CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build();
		
		FinanceFormHandler financeFormHandler = mock(FinanceFormHandler.class);
		
		setupStubs(applicationId, costsQuestions, userId, competitionId, organisationId, application, competition,
				financeFormHandler);
		
		manager.addOrganisationFinanceDetails(model, applicationId, costsQuestions, userId, form, organisationId);

		assertEquals(9, model.asMap().size());
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
		
		Long organisationType = 1L;
		String organisationTypeName = "Business";

		OrganisationTypeResource organisationTypeResource = newOrganisationTypeResource().withName(organisationTypeName).build();

		when(organisationTypeService.getForOrganisationId(organisationId)).thenReturn(restSuccess(organisationTypeResource));

		when(organisationService.getOrganisationType(userId, applicationId)).thenReturn(organisationType);
		
		when(financeHandler.getFinanceFormHandler(organisationType)).thenReturn(financeFormHandler);
		
    	when(formInputService.findApplicationInputsByQuestion(isA(Long.class))).thenReturn(asList(newFormInputResource().withType(FormInputType.LABOUR).build()));

		FinanceRowItem costItem = new LabourCost();
		when(financeFormHandler.addCostWithoutPersisting(applicationId, userId, costsQuestions.get(0).getId())).thenReturn(costItem);

		OrganisationResource organisation = newOrganisationResource().withId(organisationId).withOrganisationType(organisationTypeResource.getId()).withOrganisationTypeName(organisationTypeName).withOrganisationType().build();
		when(organisationService.getOrganisationById(organisationId)).thenReturn(organisation);
	}
}
