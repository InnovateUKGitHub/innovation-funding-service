package org.innovateuk.ifs.application.forms.questions.terms.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.*;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTermsModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private CompetitionRestService competitionRestServiceMock;
    @Mock
    private UserRestService userRestServiceMock;
    @Mock
    private OrganisationService organisationServiceMock;
    @Mock
    private QuestionStatusRestService questionStatusRestServiceMock;
    @Mock
    private SectionService sectionServiceMock;

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
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
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
        QuestionStatusResource questionStatus = newQuestionStatusResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
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
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
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
        QuestionStatusResource questionStatus = newQuestionStatusResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertFalse(actual.isCollaborativeApplication());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
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
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
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
        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.getTermsAccepted().get());
        assertEquals("you", actual.getTermsAcceptedByName().get());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn().get());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
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
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
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

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());

        assertTrue(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().get().isEmpty());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertTrue(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
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
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.OPENED)
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

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(acceptedUser.getId())
                .withMarkedAsCompleteByUserName(acceptedUser.getName())
                .build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertTrue(actual.getTermsAccepted().get());
        assertEquals(acceptedUser.getName(), actual.getTermsAcceptedByName().get());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn().get());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_notAcceptedByCollaborator() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
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
        OrganisationResource collaboratorOrganisation = newOrganisationResource().build();
        QuestionStatusResource questionStatus = newQuestionStatusResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(questionStatusRestServiceMock.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(
                Stream.of(
                        new SimpleEntry<>(organisation.getId(), singleton(termsAndConditionsSection.getId())),
                        new SimpleEntry<Long, Set<Long>>(collaboratorOrganisation.getId(), emptySet())
                )
                        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))
        );

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertFalse(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(questionStatusRestServiceMock).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisation.getId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_acceptedNoOrganisation() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        ZonedDateTime acceptedDate = now();

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();
        UserResource otherUser = newUserResource().build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
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
        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(otherUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertFalse(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().isPresent());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_applicationSubmitted() {
        String termsTemplate = "terms-template";
        boolean collaborative = true;

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.SUBMITTED)
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
        QuestionStatusResource questionStatus = newQuestionStatusResource().build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestServiceMock.findProcessRole(processRoles.get(0).getApplicationId())).thenReturn(restSuccess(processRoles));
        when(organisationServiceMock.getOrganisationForUser(currentUser.getId(), processRoles)).thenReturn(Optional.of(organisation));
        when(sectionServiceMock.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionServiceMock.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertFalse(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().isPresent());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, userRestServiceMock,
                organisationServiceMock, questionStatusRestServiceMock, sectionServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRoles.get(0).getApplicationId());
        inOrder.verify(organisationServiceMock).getOrganisationForUser(currentUser.getId(), processRoles);
        inOrder.verify(sectionServiceMock).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionServiceMock).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }
}