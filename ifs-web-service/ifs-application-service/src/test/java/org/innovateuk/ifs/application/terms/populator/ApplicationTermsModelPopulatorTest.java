package org.innovateuk.ifs.application.terms.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.terms.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTermsModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private CompetitionRestService competitionRestServiceMock;
    @Mock
    private SectionService sectionServiceMock;
    @Mock
    private UserRestService userRestServiceMock;
    @Mock
    private OrganisationService organisationServiceMock;
    @Mock
    private QuestionStatusRestService questionStatusRestServiceMock;

    @InjectMocks
    private ApplicationTermsModelPopulator populator;

    @Test
    public void populate() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        OrganisationResource organisation = newOrganisationResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.findByQuestionAndApplicationAndOrganisation(termsAndConditionsSection.getQuestions().get(0),
                application.getId(), organisation.getId())).thenReturn(restSuccess(emptyList()));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId);

        assertEquals((long)application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertFalse(actual.isTermsAccepted());
        assertNull("you", actual.getTermsAcceptedByName());
        assertNull(actual.getTermsAcceptedOn());
        assertFalse(actual.isMigratedTerms());
    }

    @Test
    public void populate_nonCollaborative() {
        String termsTemplate = "terms-template";
        boolean collaborative = false;

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        OrganisationResource organisation = newOrganisationResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.findByQuestionAndApplicationAndOrganisation(termsAndConditionsSection.getQuestions().get(0),
                application.getId(), organisation.getId())).thenReturn(restSuccess(emptyList()));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId);

        assertEquals((long)application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertFalse(actual.isCollaborativeApplication());
        assertFalse(actual.isTermsAccepted());
        assertNull("you", actual.getTermsAcceptedByName());
        assertNull(actual.getTermsAcceptedOn());
        assertFalse(actual.isMigratedTerms());
    }

    @Test
    public void populate_accepted() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        ZonedDateTime acceptedDate = now();

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        OrganisationResource organisation = newOrganisationResource().build();

        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build(1);

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.findByQuestionAndApplicationAndOrganisation(termsAndConditionsSection.getQuestions().get(0),
                application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId);

        assertEquals((long)application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isTermsAccepted());
        assertEquals("you", actual.getTermsAcceptedByName());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn());
        assertFalse(actual.isMigratedTerms());
    }

    @Test
    public void populate_migrated() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        ZonedDateTime acceptedDate = now();

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        OrganisationResource organisation = newOrganisationResource().build();

        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build(1);

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.findByQuestionAndApplicationAndOrganisation(termsAndConditionsSection.getQuestions().get(0),
                application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId);

        assertEquals((long)application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isTermsAccepted());
        assertFalse(actual.getTermsAcceptedByName().isEmpty());
        assertNull(actual.getTermsAcceptedOn());
        assertTrue(actual.isMigratedTerms());
    }

    @Test
    public void populate_acceptedByOtherUser() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        ZonedDateTime acceptedDate = now();

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();
        UserResource acceptedUser = newUserResource().withFirstName("accepted").withLastName("user").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        long questionId = 3L;
        SectionResource termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        OrganisationResource organisation = newOrganisationResource().build();

        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(acceptedUser.getId())
                .withMarkedAsCompleteByUserName(acceptedUser.getName())
                .build(1);

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.findByQuestionAndApplicationAndOrganisation(termsAndConditionsSection.getQuestions().get(0),
                application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId);

        assertEquals((long)application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isTermsAccepted());
        assertEquals(acceptedUser.getName(), actual.getTermsAcceptedByName());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn());
        assertFalse(actual.isMigratedTerms());
    }
}