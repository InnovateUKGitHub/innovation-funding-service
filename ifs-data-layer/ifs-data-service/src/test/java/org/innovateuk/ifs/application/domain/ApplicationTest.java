package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.resource.ApplicationState.CREATED;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

public class ApplicationTest {
    private Application application;

    private Competition competition;
    private String name;
    private List<ProcessRole> processRoles;
    private ApplicationState applicationState;
    private Long id;
    private List<ApplicationFinance> applicationFinances;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        name = "testApplicationName";
        applicationState = CREATED;
        competition = new Competition();
        applicationFinances = new ArrayList<>();

        processRoles = new ArrayList<>();
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());
        processRoles.add(new ProcessRole());

        application = new Application(competition, name, processRoles);
        application.setId(id);
    }

    @Test
    public void applicationShouldReturnCorrectAttributeValues() {
        Assert.assertEquals(application.getId(), id);
        Assert.assertEquals(application.getName(), name);
        Assert.assertEquals(application.getApplicationProcess().getProcessState(), applicationState);
        Assert.assertEquals(application.getProcessRoles(), processRoles);
        Assert.assertEquals(application.getCompetition(), competition);
        Assert.assertEquals(application.getApplicationFinances(), applicationFinances);
    }
    @Test
    public void addFormInputResponse() {
    	FormInput formInput = newFormInput().withQuestion(newQuestion().withMultipleStatuses(true).build()).build();
        ProcessRole processRole = newProcessRole().withOrganisationId(1L).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFormInputs(formInput).withUpdatedBy(processRole).build();

    	application.addFormInputResponse(formInputResponse, processRole);
    	
    	assertEquals(1, application.getFormInputResponses().size());
    	assertEquals(formInputResponse, application.getFormInputResponses().get(0));
    }
    
    @Test
    public void addFormInputResponsesForDifferentInputs() {
        ProcessRole processRole = newProcessRole().withOrganisationId(1L).build();
    	FormInput formInput1 = newFormInput().withQuestion(newQuestion().withMultipleStatuses(false).build()).build();
    	FormInputResponse formInputResponse1 = newFormInputResponse().withFormInputs(formInput1).withUpdatedBy(processRole).build();
    	FormInput formInput2 = newFormInput().withQuestion(newQuestion().withMultipleStatuses(false).build()).build();
    	FormInputResponse formInputResponse2 = newFormInputResponse().withFormInputs(formInput2).withUpdatedBy(processRole).build();


        application.addFormInputResponse(formInputResponse1, processRole);
    	application.addFormInputResponse(formInputResponse2, processRole);
    	
    	assertEquals(2, application.getFormInputResponses().size());
    	assertEquals(formInputResponse1, application.getFormInputResponses().get(0));
    	assertEquals(formInputResponse2, application.getFormInputResponses().get(1));
    }
    
    @Test
    public void addFormInputResponsesForSameInputsAndProcessRoles() {
    	FormInput formInput = newFormInput().withQuestion(newQuestion().withMultipleStatuses(true).build()).build();
        ProcessRole processRole = newProcessRole().withOrganisationId(1L).build();
    	FormInputResponse formInputResponse1 = newFormInputResponse().withFormInputs(formInput).withValue("1").withUpdatedBy(processRole).build();
    	FormInputResponse formInputResponse2 = newFormInputResponse().withFormInputs(formInput).withValue("2").withUpdatedBy(processRole).build();
    	
    	application.addFormInputResponse(formInputResponse1, processRole);
    	application.addFormInputResponse(formInputResponse2, processRole);
    	
    	assertEquals(1, application.getFormInputResponses().size());
    	assertEquals(formInputResponse1, application.getFormInputResponses().get(0));
    	assertEquals("2", application.getFormInputResponses().get(0).getValue());
    }

    @Test
    public void addFormInputResponsesForSameInputsAndDifferentProcessRoles() {
        FormInput formInput = newFormInput().withQuestion(newQuestion().withMultipleStatuses(true).build()).build();
        ProcessRole processRole1 = newProcessRole().withOrganisationId(1L).build();
        ProcessRole processRole2 = newProcessRole().withOrganisationId(2L).build();
        FormInputResponse formInputResponse1 = newFormInputResponse().withFormInputs(formInput).withUpdatedBy(processRole1).build();
        FormInputResponse formInputResponse2 = newFormInputResponse().withFormInputs(formInput).withUpdatedBy(processRole2).build();

        application.addFormInputResponse(formInputResponse1, processRole1);
        application.addFormInputResponse(formInputResponse2, processRole2);

        assertEquals(2, application.getFormInputResponses().size());
        assertEquals(formInputResponse1, application.getFormInputResponses().get(0));
        assertEquals(formInputResponse2, application.getFormInputResponses().get(1));
    }

    @Test(expected=IllegalStateException.class)
    public void addingInnovationAreaAndThenNotApplicableShouldResultInIllegalStateException() {
        Application application = new Application();
        application.setInnovationArea(newInnovationArea().build());
        application.setNoInnovationAreaApplicable(true);
    }

    @Test(expected=IllegalStateException.class)
    public void addingNotApplicableAndThenInnovationAreaShouldResultInIllegalStateException() {
        Application application = new Application();
        application.setNoInnovationAreaApplicable(true);
        application.setInnovationArea(newInnovationArea().build());
    }

    @Test
    public void applicationFundingDecisionIsNotChangeable() {
        Application application = new Application();
        application.setFundingDecision(FundingDecisionStatus.FUNDED);
        application.setManageFundingEmailDate(ZonedDateTime.now());
        assertFalse(application.applicationFundingDecisionIsChangeable());
    }

    @Test
    public void applicationFundingDecisionIsChangeableNotFunded() {
        Application application = new Application();
        application.setManageFundingEmailDate(ZonedDateTime.now());
        application.setFundingDecision(FundingDecisionStatus.ON_HOLD);
        assertTrue(application.applicationFundingDecisionIsChangeable());
    }

    @Test
    public void applicationFundingDecisionIsChangeableNoFundingEmail() {
        Application application = new Application();
        application.setManageFundingEmailDate(null);
        application.setFundingDecision(FundingDecisionStatus.FUNDED);
        assertTrue(application.applicationFundingDecisionIsChangeable());
    }

    @Test
    public void applicationFundingDecisionIsChangeable() {
        assertTrue(new Application().applicationFundingDecisionIsChangeable());
    }

    @Test
    public void applicationGetMaxMilestoneMonthEmptyWhenNoMilestones() {
        Application application  = new Application();
        assertFalse(application.getMaxMilestoneMonth().isPresent());
    }

    @Test
    public void applicationGetMaxMilestoneMonth() {
        Integer maxMilestoneMonth = 20;
        Application application = newApplication()
                .withApplicationFinancesList(asList(
                        newApplicationFinance().withMilestones(asList(
                                newApplicationProcurementMilestone().withMonth(1).build(),
                                newApplicationProcurementMilestone().withMonth(10).build(),
                                newApplicationProcurementMilestone().withMonth((Integer) null).build() // This should not happen but check it is handled
                                )).build(),
                        newApplicationFinance().withMilestones(asList(
                                newApplicationProcurementMilestone().withMonth(maxMilestoneMonth).build(), // The maximum
                                newApplicationProcurementMilestone().withMonth(-1).build(), // This should not happen but check it is handled
                                newApplicationProcurementMilestone().withMonth(0).build()
                        )).build()
                )).build();
        assertTrue(application.getMaxMilestoneMonth().isPresent());
        assertEquals(application.getMaxMilestoneMonth().get(), maxMilestoneMonth);
    }
}