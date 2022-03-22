package org.innovateuk.ifs.application.forms.questions.terms.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
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
import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTermsModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private SectionService sectionService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;
    @Mock
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    @InjectMocks
    private ApplicationTermsModelPopulator populator;

    private final String termsTemplate = "terms-template";
    private final long organisationId = 1L;
    private final boolean collaborative = true;
    private final long questionId = 3L;
    private final ZonedDateTime acceptedDate = now();

    private UserResource currentUser;
    private GrantTermsAndConditionsResource grantTermsAndConditions;
    private CompetitionResource competition;
    private CompetitionResource competitionFinanceType;
    private CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource;
    private OrganisationResource organisation;
    private QuestionStatusResource questionStatus;
    private ApplicationResource createdApplication;
    private ApplicationResource applicationForFinanceType;
    private List<ProcessRoleResource> processRoles;
    private SectionResource termsAndConditionsSection;

    @Before
    public void setup() {
        currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();
        grantTermsAndConditions = new GrantTermsAndConditionsResource("name", termsTemplate, 1);

        competitionFinanceType = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
                .build();

        competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
                .withNonFinanceType(true)
                .build();

        organisation = newOrganisationResource().build();
        questionStatus = newQuestionStatusResource().build();

        createdApplication = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
                .build();
        applicationForFinanceType = newApplicationResource()
                .withCompetition(competitionFinanceType.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.CREATED)
                .build();


        processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(createdApplication.getId())
                .build(1);

        termsAndConditionsSection = newSectionResource()
                .withQuestions(singletonList(questionId))
                .build();


        competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource().build();

        when(applicationRestService.getApplicationById(createdApplication.getId())).thenReturn(restSuccess(createdApplication));
        when(applicationRestService.getApplicationById(applicationForFinanceType.getId())).thenReturn(restSuccess(applicationForFinanceType));

        when(competitionRestService
                .getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionRestService
                .getCompetitionById(competitionFinanceType.getId())).thenReturn(restSuccess(competitionFinanceType));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, applicationForFinanceType.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionService.getSectionsForCompetitionByType(competitionFinanceType.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionService.getCompletedSectionsByOrganisation(createdApplication.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));
        when(sectionService.getCompletedSectionsByOrganisation(applicationForFinanceType.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        when(questionRestService.findById(questionId)).thenReturn(restSuccess(newQuestionResource().build()));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

    }

    @Test
    public void populate() {

        QuestionResource subsidyBasisQuestion = newQuestionResource().build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withNorthernIrelandDeclaration(false)
                .build();

        when(applicationFinanceRestService.getApplicationFinance(applicationForFinanceType.getId(), organisationId)).thenReturn(restSuccess(applicationFinanceResource));

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionFinanceType.getId(), SUBSIDY_BASIS)).thenReturn(restSuccess(subsidyBasisQuestion));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(
                        subsidyBasisQuestion.getId(), applicationForFinanceType.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(newQuestionStatusResource().withMarkedAsComplete(false).build())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, applicationForFinanceType.getId(), questionId, organisationId, false);

        assertEquals((Long) applicationForFinanceType.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());
        assertEquals(String.format("/application/%d/form/question/%d", applicationForFinanceType.getId(), subsidyBasisQuestion.getId()), actual.getSubsidyBasisQuestionUrl());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(applicationForFinanceType.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competitionFinanceType.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, applicationForFinanceType.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competitionFinanceType.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(applicationForFinanceType.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_nonCollaborative() {
        boolean collaborative = false;

        UserResource currentUser = newUserResource().withFirstName("tom").withLastName("baldwin").build();

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withCompetitionStatus(OPEN)
                .withNonFinanceType(true)
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

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService
                .getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(newQuestionResource().build()));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, organisationId,  false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertFalse(actual.isCollaborativeApplication());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_accepted() {

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();

        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(newOrganisationResource().build()));

        ApplicationTermsViewModel actual = populator.populate(currentUser, createdApplication.getId(), questionId, organisationId,  false);

        assertEquals((Long) createdApplication.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.getTermsAccepted().get());
        assertEquals("you", actual.getTermsAcceptedByName().get());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn().get());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(createdApplication.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(createdApplication.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_migrated() {

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();

        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));

        ApplicationTermsViewModel actual = populator.populate(currentUser, createdApplication.getId(), questionId, organisationId,  false);

        assertEquals((Long) createdApplication.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());

        assertTrue(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().get().isEmpty());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertTrue(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(createdApplication.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(createdApplication.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_acceptedByOtherUser() {

        UserResource acceptedUser = newUserResource().withFirstName("accepted").withLastName("user").build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(OPENED)
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(acceptedUser.getId())
                .withMarkedAsCompleteByUserName(acceptedUser.getName())
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, organisationId,  false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertTrue(actual.getTermsAccepted().get());
        assertEquals(acceptedUser.getName(), actual.getTermsAcceptedByName().get());
        assertEquals(acceptedDate, actual.getTermsAcceptedOn().get());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_notAcceptedByCollaborator() {

        OrganisationResource collaboratorOrganisation = newOrganisationResource().build();

        when(sectionService.getCompletedSectionsByOrganisation(createdApplication.getId())).thenReturn(
                Stream.of(
                        new SimpleEntry<>(organisation.getId(), singleton(termsAndConditionsSection.getId())),
                        new SimpleEntry<Long, Set<Long>>(collaboratorOrganisation.getId(), emptySet())
                )
                        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))
        );

        ApplicationTermsViewModel actual = populator.populate(currentUser, createdApplication.getId(), questionId, organisationId,  false);

        assertEquals((Long) createdApplication.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertFalse(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(createdApplication.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, createdApplication.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(createdApplication.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_acceptedNoOrganisation() {

        UserResource otherUser = newUserResource().build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        QuestionStatusResource questionStatus = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteOn(acceptedDate)
                .withMarkedAsCompleteByUserId(currentUser.getId())
                .withMarkedAsCompleteByUserName(currentUser.getName())
                .build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withNorthernIrelandDeclaration(false)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(otherUser, application.getId(), questionId, organisationId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertFalse(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().isPresent());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_applicationSubmitted() {

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(currentUser)
                .withApplication(application.getId())
                .build(1);

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withNorthernIrelandDeclaration(false)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, organisationId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertEquals(termsTemplate, actual.getCompetitionTermsTemplate());
        assertTrue(actual.isCollaborativeApplication());
        assertFalse(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().isPresent());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_other_terms() {
        String other_termsTemplate = "other-terms-template";

        GrantTermsAndConditionsResource otherGrantTermsAndConditions =
                new GrantTermsAndConditionsResource("other_termsTemplate", other_termsTemplate, 1);

        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withOtherFundingRulesTermsAndConditions(otherGrantTermsAndConditions)
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

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withNorthernIrelandDeclaration(true)
                .build();
        QuestionResource subsidyBasisQuestion = newQuestionResource().build();

        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisationId)).thenReturn(restSuccess(applicationFinanceResource));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService
                .getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId))
                .thenReturn(restSuccess(Optional.of(questionStatus)));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(termsAndConditionsSection));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(termsAndConditionsSection.getId())));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(newQuestionResource().build()));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS)).thenReturn(restSuccess(subsidyBasisQuestion));
        when(questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(
                subsidyBasisQuestion.getId(), application.getId(), organisation.getId()))
                .thenReturn(restSuccess(Optional.of(newQuestionStatusResource().withMarkedAsComplete(false).build())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionId, organisationId, false);

        assertEquals((Long) application.getId(), actual.getApplicationId());
        assertTrue(actual.isCollaborativeApplication());
        assertTrue(actual.isShowHeaderAndFooter());
        assertFalse(actual.getTermsAccepted().get());
        assertFalse(actual.getTermsAcceptedByName().isPresent());
        assertFalse(actual.getTermsAcceptedOn().isPresent());
        assertTrue(actual.isTermsAcceptedByAllOrganisations());
        assertFalse(actual.isMigratedTerms());
        assertTrue(other_termsTemplate.equals(actual.getCompetitionTermsTemplate()));

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionStatusRestService, sectionService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(competition.getId());
        inOrder.verify(questionStatusRestService).getMarkedAsCompleteByQuestionApplicationAndOrganisation(questionId, application.getId(), organisationId);
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void populate_applicationInThirdPartyCompetition() {

        CompetitionThirdPartyConfigResource thirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withTermsAndConditionsLabel("Test label")
                .withTermsAndConditionsGuidance("Test guidance")
                .withProjectCostGuidanceUrl("https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance")
                .build();
        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withTemplate("third-party-terms-and-conditions")
                .withName("Third Party")
                .build();
        CompetitionResource thirdPartyCompetition = newCompetitionResource()
                .withCompetitionStatus(OPEN)
                .withCompetitionThirdPartyConfig(thirdPartyConfigResource)
                .withName("Third party competition")
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationNumber("88L")
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(thirdPartyCompetition.getId())
                .withLeadOrganisationId(organisation.getId())
                .withApplicationState(OPENED)
                .withName("Third party competition application")
                .withCollaborativeProject(false)
                .build();
        QuestionResource questionResource = newQuestionResource()
                .withId(44L)
                .withCompetition(thirdPartyCompetition.getId())
                .withQuestionSetupType(QuestionSetupType.TERMS_AND_CONDITIONS)
                .withName("Terms and conditions")
                .build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .build();
        SectionResource sectionResource = newSectionResource()
                .withCompetition(thirdPartyCompetition.getId())
                .withQuestions(singletonList(questionId))
                .withType(TERMS_AND_CONDITIONS)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService
                .getCompetitionById(application.getCompetition())).thenReturn(restSuccess(thirdPartyCompetition));
        when(questionRestService.findById(questionResource.getId())).thenReturn(restSuccess(questionResource));
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinanceResource));
        when(sectionService.getSectionsForCompetitionByType(thirdPartyCompetition.getId(), TERMS_AND_CONDITIONS)).thenReturn(singletonList(sectionResource));
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(singletonMap(organisation.getId(), singleton(sectionResource.getId())));

        ApplicationTermsViewModel actual = populator.populate(currentUser, application.getId(), questionResource.getId(), organisation.getId(), true);

        assertEquals(thirdPartyCompetition.getTermsAndConditions().isProcurementThirdParty(), actual.isThirdPartyProcurementCompetition());
        assertEquals(thirdPartyCompetition.getTermsAndConditions().getTemplate(), actual.getCompetitionTermsTemplate());
        assertEquals(thirdPartyConfigResource.getTermsAndConditionsLabel(), actual.getThirdPartyConfig().getTermsAndConditionsLabel());
        assertEquals(thirdPartyConfigResource.getTermsAndConditionsGuidance(), actual.getThirdPartyConfig().getTermsAndConditionsGuidance());

        InOrder inOrder = inOrder(applicationRestService, competitionRestService
                , questionRestService, competitionThirdPartyConfigRestService,
                organisationRestService, applicationFinanceRestService, sectionService, questionStatusRestService);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(competitionRestService
        ).getCompetitionById(thirdPartyCompetition.getId());
        inOrder.verify(questionRestService).findById(questionResource.getId());
        inOrder.verify(applicationFinanceRestService).getApplicationFinance(application.getId(), organisation.getId());
        inOrder.verify(sectionService).getSectionsForCompetitionByType(thirdPartyCompetition.getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        inOrder.verifyNoMoreInteractions();
    }
}