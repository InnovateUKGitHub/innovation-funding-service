package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
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
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

public class ApplicationTest {
    private Application application;

    private Competition competition;
    private Competition expressionOfInterestCompetitionEvidenceRequired;
    private Competition expressionOfInterestCompetitionNoEvidenceRequired;
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
        competition = newCompetition().build();

        long eoiEvidenceConfigIdEvidenceRequired = 8L;
        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfigEvidenceRequired = new CompetitionEoiEvidenceConfig();
        competitionEoiEvidenceConfigEvidenceRequired.setId(eoiEvidenceConfigIdEvidenceRequired);
        competitionEoiEvidenceConfigEvidenceRequired.setEvidenceRequired(true);
        competitionEoiEvidenceConfigEvidenceRequired.setEvidenceTitle("Evidence title");
        competitionEoiEvidenceConfigEvidenceRequired.setEvidenceGuidance("Evidence guidance");

        expressionOfInterestCompetitionEvidenceRequired = newCompetition()
                .withEnabledForExpressionOfInterest(true)
                .withCompetitionEoiEvidenceConfig(competitionEoiEvidenceConfigEvidenceRequired)
                .build();

        long eoiEvidenceConfigIdNoEvidenceRequired = 8L;
        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfigNoEvidenceRequired = new CompetitionEoiEvidenceConfig();
        competitionEoiEvidenceConfigNoEvidenceRequired.setId(eoiEvidenceConfigIdNoEvidenceRequired);
        competitionEoiEvidenceConfigNoEvidenceRequired.setEvidenceRequired(false);
        competitionEoiEvidenceConfigNoEvidenceRequired.setEvidenceTitle("Evidence title");
        competitionEoiEvidenceConfigNoEvidenceRequired.setEvidenceGuidance("Evidence guidance");

        expressionOfInterestCompetitionNoEvidenceRequired = newCompetition()
                .withEnabledForExpressionOfInterest(true)
                .withCompetitionEoiEvidenceConfig(competitionEoiEvidenceConfigNoEvidenceRequired)
                .build();

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
        assertEquals(application.getId(), id);
        assertEquals(application.getName(), name);
        assertEquals(application.getApplicationProcess().getProcessState(), applicationState);
        assertEquals(application.getProcessRoles(), processRoles);
        assertEquals(application.getCompetition(), competition);
        assertEquals(application.getApplicationFinances(), applicationFinances);
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

        newApplication()
                .withInnovationArea(newInnovationArea().build())
                .withNoInnovationAreaApplicable(true)
                .build();
    }

    @Test(expected=IllegalStateException.class)
    public void addingNotApplicableAndThenInnovationAreaShouldResultInIllegalStateException() {

        newApplication()
                .withNoInnovationAreaApplicable(true)
                .withInnovationArea(newInnovationArea().build())
                .build();
    }

    @Test
    public void applicationDecisionIsNotChangeable() {

        Application application = newApplication()
                .withDecision(DecisionStatus.FUNDED)
                .withManageDecisionEmailDate(ZonedDateTime.now())
                .build();

        assertFalse(application.applicationDecisionIsChangeable());
    }

    @Test
    public void applicationDecisionIsChangeableNotFunded() {

        Application application = newApplication()
                .withManageDecisionEmailDate(ZonedDateTime.now())
                .withDecision(DecisionStatus.ON_HOLD)
                .build();

        assertTrue(application.applicationDecisionIsChangeable());
    }

    @Test
    public void applicationDecisionIsChangeableNoFundingEmail() {

        Application application = newApplication()
                .withManageDecisionEmailDate(null)
                .withDecision(DecisionStatus.FUNDED)
                .build();

        assertTrue(application.applicationDecisionIsChangeable());
    }

    @Test
    public void applicationDecisionIsChangeable() {
        assertTrue(new Application().applicationDecisionIsChangeable());
    }

    @Test
    public void applicationGetMaxMilestoneMonthEmptyWhenNoMilestones() {

        Application application = newApplication()
                .build();

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

    @Test
    public void applicationExpressionOfInterestEvidenceNotRequired() {

        Application application = newApplication()
                .withCompetition(expressionOfInterestCompetitionNoEvidenceRequired)
                .build();

        assertFalse(application.expressionOfInterestEvidenceDocumentRequired());
    }

    @Test
    public void applicationExpressionOfInterestEvidenceUploadedAndNotSubmitted() {

        FileEntry fileEntry = newFileEntry().build();

        long eoiEvidenceConfigId = 8L;
        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig = new CompetitionEoiEvidenceConfig();
        competitionEoiEvidenceConfig.setId(eoiEvidenceConfigId);
        competitionEoiEvidenceConfig.setEvidenceRequired(true);
        competitionEoiEvidenceConfig.setEvidenceTitle("Evidence title");
        competitionEoiEvidenceConfig.setEvidenceGuidance("Evidence guidance");

        Application application = newApplication()
                .withCompetition(expressionOfInterestCompetitionEvidenceRequired)
                .withApplicationEoiEvidenceResponse(
                        ApplicationEoiEvidenceResponse.builder()
                                .fileEntry(fileEntry)
                                .applicationEoiEvidenceProcess(ApplicationEoiEvidenceProcess.builder()
                                        .processState(ApplicationEoiEvidenceState.CREATED)
                                        .build())
                                .build())
                .build();

        assertFalse(application.isApplicationExpressionOfInterestEvidenceResponseReceived());
    }

    @Test
    public void applicationExpressionOfInterestEvidenceUploadedAndSubmitted() {

        FileEntry fileEntry = newFileEntry().build();

        Application application = newApplication()
                .withCompetition(expressionOfInterestCompetitionEvidenceRequired)
                .withApplicationEoiEvidenceResponse(
                        ApplicationEoiEvidenceResponse.builder()
                                .fileEntry(fileEntry)
                                .applicationEoiEvidenceProcess(ApplicationEoiEvidenceProcess.builder()
                                        .processState(ApplicationEoiEvidenceState.SUBMITTED)
                                        .build())
                                .build())
                .build();

        assertTrue(application.isApplicationExpressionOfInterestEvidenceResponseReceived());
    }
}