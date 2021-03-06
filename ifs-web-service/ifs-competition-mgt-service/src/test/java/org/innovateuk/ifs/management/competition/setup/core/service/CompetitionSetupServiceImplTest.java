package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.innovateuk.ifs.management.competition.setup.application.form.DetailsForm;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupPopulator;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionStateSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.viewmodel.ProjectEligibilityViewModel;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.viewmodel.AdditionalModelViewModel;
import org.innovateuk.ifs.management.competition.setup.initialdetail.populator.InitialDetailsModelPopulator;
import org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel.InitialDetailsViewModel;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupServiceImplTest {

    private static final Long COMPETITION_ID = 3422L;

    @InjectMocks
    private CompetitionSetupServiceImpl service;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Mock
    private CompetitionInviteRestService competitionInviteRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Before
    public void setup() {
        Map<CompetitionSetupSection, Optional<Boolean>> sectionStatuses = asMap(INITIAL_DETAILS, Optional.of(Boolean.TRUE));
        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID))
                 .thenReturn(restSuccess(sectionStatuses));
    }

    @Test
    public void populateCompetitionSectionModelAttributesNoMatchingFormPopulator() {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionCode("code")
                .withName("name")
                .withSetupComplete(false)
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(InitialDetailsModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(INITIAL_DETAILS);
        when(matchingPopulator.populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class)))
                .thenReturn(new InitialDetailsViewModel(getBasicGeneralSetupView(INITIAL_DETAILS, competition),
                        emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), false));
        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupSection section = INITIAL_DETAILS;

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, null, section);

        verifyCommonModelAttributes(viewModel, competition, section);
        assertEquals("section-initial", viewModel.getGeneral().getCurrentSectionFragment());
    }

    @Test
    public void populateCompetitionSectionModelAttributesEligibility() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withSetupComplete(false)
                .withName("name")
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.PROJECT_ELIGIBILITY);
        when(matchingPopulator.populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class)))
                .thenReturn(new ProjectEligibilityViewModel(getBasicGeneralSetupView(CompetitionSetupSection.PROJECT_ELIGIBILITY, competition), new ResearchParticipationAmount[]{},
                        new CollaborationLevel[]{}, emptyList(), "", new FundingLevel[]{}, emptyList(), ""));
        CompetitionSetupSectionModelPopulator notMatchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(notMatchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.MILESTONES);

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator, notMatchingPopulator));

        CompetitionSetupSection section = CompetitionSetupSection.PROJECT_ELIGIBILITY;

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, null, section);

        verifyCommonModelAttributes(viewModel, competition, section);
        assertEquals("section-project-eligibility", viewModel.getGeneral().getCurrentSectionFragment());

        verify(matchingPopulator).populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class));
        verify(notMatchingPopulator, never()).populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class));
    }

    private void verifyCommonModelAttributes(CompetitionSetupViewModel viewModel, CompetitionResource competition,
                                             CompetitionSetupSection section) {
        assertEquals(Boolean.FALSE, viewModel.getGeneral().isInitialComplete());
        assertEquals(Boolean.TRUE, viewModel.getGeneral().isEditable());
        assertEquals(competition, viewModel.getGeneral().getCompetition());
        assertEquals(section, viewModel.getGeneral().getCurrentSection());
        assertArrayEquals(CompetitionSetupSection.values(), viewModel.getGeneral().getAllSections());
        assertEquals(Boolean.FALSE, viewModel.getGeneral().getState().isPreventEdit());
        assertEquals(Boolean.FALSE, viewModel.getGeneral().getState().isSetupAndLive());
        assertEquals(Boolean.FALSE, viewModel.getGeneral().getState().isSetupComplete());
        assertEquals(CompetitionStatus.COMPETITION_SETUP, viewModel.getGeneral().getState().getCompetitionStatus());
    }

    @Test
    public void getSectionFormData() {
        CompetitionResource competitionResource = newCompetitionResource().build();

        CompetitionSetupFormPopulator matchingPopulator = mock(CompetitionSetupFormPopulator.class);
        when(matchingPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
        CompetitionSetupForm matchingForm = mock(CompetitionSetupForm.class);
        when(matchingPopulator.populateForm(competitionResource)).thenReturn(matchingForm);

        CompetitionSetupFormPopulator otherPopulator = mock(CompetitionSetupFormPopulator.class);
        when(otherPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
        CompetitionSetupForm otherForm = mock(CompetitionSetupForm.class);
        when(otherPopulator.populateForm(competitionResource)).thenReturn(otherForm);

        service.setCompetitionSetupFormPopulators(asList(matchingPopulator, otherPopulator));

        CompetitionSetupFormPopulator result = service.getSectionFormPopulator(CompetitionSetupSection.ADDITIONAL_INFO);

        assertEquals(matchingPopulator, result);
    }

    @Test
    public void saveSection() {
        CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionSetupRestService.getSectionStatuses(competitionResource.getId())).thenReturn(restSuccess(asMap(
                INITIAL_DETAILS, Optional.of(true),
                CompetitionSetupSection.ADDITIONAL_INFO, Optional.of(false))));

        CompetitionSetupSectionUpdater matchingSaver = mock(CompetitionSetupSectionUpdater.class);
        when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
        when(matchingSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(true);

        CompetitionSetupSectionUpdater otherSaver = mock(CompetitionSetupSectionUpdater.class);
        when(otherSaver.sectionToSave()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
        when(otherSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(false);

        when(matchingSaver.saveSection(competitionResource, competitionSetupForm)).thenReturn(serviceSuccess());

        when(competitionSetupRestService.markSectionComplete(competitionResource.getId(), CompetitionSetupSection.ADDITIONAL_INFO))
                .thenReturn(restSuccess());

        service.setCompetitionSetupSectionSavers(asList(matchingSaver, otherSaver));

        service.saveCompetitionSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);

        verify(matchingSaver).saveSection(competitionResource, competitionSetupForm);
        verify(otherSaver, never()).saveSection(competitionResource, competitionSetupForm);
    }

    @Test(expected = IllegalStateException.class)
    public void saveCompetitionSetupSection_initialDetailsMustBeComplete() throws Exception {
        CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();
        CompetitionSetupSection section = CompetitionSetupSection.ADDITIONAL_INFO;

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID))
                .thenReturn(restSuccess(asMap(CompetitionSetupSection.INITIAL_DETAILS, Optional.empty())));

        service.saveCompetitionSetupSection(competitionSetupForm, competition, section);
    }


    @Test(expected = IllegalStateException.class)
    public void saveCompetitionSetupSubsection_initialDetailsMustBeComplete() throws Exception {
        CompetitionSetupForm competitionSetupForm = new DetailsForm();
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();
        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        CompetitionSetupSubsection subsection = CompetitionSetupSubsection.APPLICATION_DETAILS;

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID))
                .thenReturn(restSuccess(asMap(CompetitionSetupSection.INITIAL_DETAILS, Optional.empty())));

        service.saveCompetitionSetupSubsection(competitionSetupForm, competition, section, subsection);
    }

    @Test
    public void isCompetitionReadyToOpen() {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withStartDate(ZonedDateTime.now().plusDays(1)).build();

        Map<CompetitionSetupSection, Optional<Boolean>> testSectionStatus = asMap(
                INITIAL_DETAILS, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.ADDITIONAL_INFO, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.PROJECT_ELIGIBILITY, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.MILESTONES, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.APPLICATION_FORM, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.ASSESSORS, Optional.of(Boolean.FALSE),
                CompetitionSetupSection.CONTENT, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.TERMS_AND_CONDITIONS, Optional.of(Boolean.TRUE),
                CompetitionSetupSection.FUNDING_ELIGIBILITY, Optional.of(Boolean.TRUE)
        );

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID)).thenReturn(restSuccess(testSectionStatus));

        assertTrue(service.isCompetitionReadyToOpen(competitionResource));
    }

    @Test
    public void isCompetitionReadyToOpenFailure() {
        Map<CompetitionSetupSection, Optional<Boolean>> testSectionStatus = new HashMap<>();
        testSectionStatus.put(INITIAL_DETAILS, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Optional.of(Boolean.FALSE));
        testSectionStatus.put(CompetitionSetupSection.PROJECT_ELIGIBILITY, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.TERMS_AND_CONDITIONS, Optional.of(Boolean.TRUE));

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withStartDate(ZonedDateTime.now().plusDays(1)).build();

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID)).thenReturn(restSuccess(testSectionStatus));

        assertFalse(service.isCompetitionReadyToOpen(competitionResource));
    }

    @Test
    public void setCompetitionAsReadyToOpenWhenReadyToOpen() {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.READY_TO_OPEN)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        assertTrue(service.setCompetitionAsReadyToOpen(COMPETITION_ID).isSuccess());

        verify(competitionRestService, only()).getCompetitionById(COMPETITION_ID);
        verifyNoMoreInteractions(competitionSetupRestService);
    }

    @Test
    public void setCompetitionAsReadyToOpenSuccess() {
        Map<CompetitionSetupSection, Optional<Boolean>> testSectionStatus = new HashMap<>();
        testSectionStatus.put(INITIAL_DETAILS, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.PROJECT_ELIGIBILITY, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.ASSESSORS, Optional.of(Boolean.FALSE));
        testSectionStatus.put(CompetitionSetupSection.CONTENT, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.TERMS_AND_CONDITIONS, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.FUNDING_ELIGIBILITY, Optional.of(Boolean.TRUE));
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID)).thenReturn(restSuccess(testSectionStatus));
        when(competitionSetupRestService.markAsSetup(COMPETITION_ID)).thenReturn(restSuccess());
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        service.setCompetitionAsReadyToOpen(COMPETITION_ID).getSuccess();

        verify(competitionRestService, only()).getCompetitionById(COMPETITION_ID);
        verify(competitionSetupRestService, times(1)).getSectionStatuses(COMPETITION_ID);
        verify(competitionSetupRestService, times(1)).markAsSetup(COMPETITION_ID);
        verifyNoMoreInteractions(competitionSetupRestService);
    }

    @Test
    public void setCompetitionAsReadyToOpenFail() {
        Map<CompetitionSetupSection, Optional<Boolean>> testSectionStatus = new HashMap<>();
        testSectionStatus.put(INITIAL_DETAILS, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Optional.empty());
        testSectionStatus.put(CompetitionSetupSection.PROJECT_ELIGIBILITY, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Optional.of(Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM,Optional.of( Boolean.TRUE));
        testSectionStatus.put(CompetitionSetupSection.TERMS_AND_CONDITIONS, Optional.of(Boolean.TRUE));
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupRestService.getSectionStatuses(COMPETITION_ID)).thenReturn(restSuccess(testSectionStatus));
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        ServiceResult<Void> updateResult = service.setCompetitionAsReadyToOpen(COMPETITION_ID);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(
                new org.innovateuk.ifs.commons.error.Error("competition.setup.not.ready.to.open", BAD_REQUEST)));

        verify(competitionRestService, only()).getCompetitionById(COMPETITION_ID);
        verify(competitionSetupRestService, times(1)).getSectionStatuses(COMPETITION_ID);
        verifyNoMoreInteractions(competitionSetupRestService);
    }

    @Test
    public void populateModel() {
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

        CompetitionSetupSection competitionSetupSection = CompetitionSetupSection.ADDITIONAL_INFO;

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(true)
                .withStartDate(yesterday)
                .withFundersPanelDate(yesterday)
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(competitionSetupSection);
        when(matchingPopulator.populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class)))
                .thenReturn(new AdditionalModelViewModel(getBasicGeneralSetupView(competitionSetupSection, competition)));

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, null, competitionSetupSection);

        assertEquals(false, viewModel.getGeneral().getState().isPreventEdit());
        assertEquals(false, viewModel.getGeneral().getState().isSetupAndLive());
        assertEquals(false, viewModel.getGeneral().getState().isSetupComplete());
        assertEquals(CompetitionStatus.COMPETITION_SETUP, viewModel.getGeneral().getState().getCompetitionStatus());
    }

    @Test
    public void populateModel_competitionNotSetupAndLive() {
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

        CompetitionSetupSection competitionSetupSection = CompetitionSetupSection.ADDITIONAL_INFO;

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(false)
                .withFundersPanelDate(tomorrow)
                .withStartDate(yesterday)
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(competitionSetupSection);
        when(matchingPopulator.populateModel(nullable(GeneralSetupViewModel.class), nullable(CompetitionResource.class)))
                .thenReturn(new AdditionalModelViewModel(getBasicGeneralSetupView(competitionSetupSection, competition)));

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, null, competitionSetupSection);

        assertEquals(false, viewModel.getGeneral().getState().isPreventEdit());
        assertEquals(false, viewModel.getGeneral().getState().isSetupAndLive());
        assertEquals(false, viewModel.getGeneral().getState().isSetupComplete());
        assertEquals(CompetitionStatus.COMPETITION_SETUP, viewModel.getGeneral().getState().getCompetitionStatus());
    }

    @Test
    public void deleteCompetition() {
        CompetitionInviteStatisticsResource competitionInviteStatisticsResource =
                newCompetitionInviteStatisticsResource()
                        .build();

        when(competitionInviteRestService.getInviteStatistics(COMPETITION_ID)).thenReturn(restSuccess(
                competitionInviteStatisticsResource));
        when(competitionSetupRestService.delete(COMPETITION_ID)).thenReturn(restSuccess());

        service.deleteCompetition(COMPETITION_ID).getSuccess();

        InOrder inOrder = inOrder(competitionInviteRestService, competitionSetupRestService);
        inOrder.verify(competitionInviteRestService).getInviteStatistics(COMPETITION_ID);
        inOrder.verify(competitionSetupRestService).delete(COMPETITION_ID);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteCompetition_assessmentInvitesExist() {
        CompetitionInviteStatisticsResource competitionInviteStatisticsResource =
                newCompetitionInviteStatisticsResource()
                        .withInviteList(1)
                        .build();

        when(competitionInviteRestService.getInviteStatistics(COMPETITION_ID)).thenReturn(restSuccess(
                competitionInviteStatisticsResource));
        when(competitionSetupRestService.delete(COMPETITION_ID)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.deleteCompetition(COMPETITION_ID);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED));

        verify(competitionInviteRestService, only()).getInviteStatistics(COMPETITION_ID);
        verify(competitionSetupRestService, never()).delete(isA(Long.class));
    }

    @Test
    public void getNextSetupSection() {
        CompetitionSetupForm competitionSetupForm = new CompletionStageForm();
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE)
                .build();

        CompetitionSetupSectionUpdater matchingSaver = mock(CompetitionSetupSectionUpdater.class);
        when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.COMPLETION_STAGE);
        when(matchingSaver.supportsForm(CompletionStageForm.class)).thenReturn(true);

        when(matchingSaver.getNextSection(competitionSetupForm, competitionResource, CompetitionSetupSection.COMPLETION_STAGE)).thenReturn(CompetitionSetupSection.MILESTONES.getPath());

        service.setCompetitionSetupSectionSavers(singleton(matchingSaver));

        service.getNextSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.COMPLETION_STAGE);

        verify(matchingSaver).getNextSection(competitionSetupForm, competitionResource, CompetitionSetupSection.COMPLETION_STAGE);
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionSetupSection section, CompetitionResource competition) {
        GeneralSetupViewModel generalSetupView = new GeneralSetupViewModel(Boolean.TRUE, false, competition, section, CompetitionSetupSection.values(), Boolean.FALSE, Boolean.FALSE);
        generalSetupView.setCurrentSectionFragment("section-" + section.getPath());
        generalSetupView.setState(new CompetitionStateSetupViewModel(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, CompetitionStatus.COMPETITION_SETUP));

        return generalSetupView;
    }
}
