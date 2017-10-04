package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.InitialDetailsModelPopulator;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSectionSaver;
import org.innovateuk.ifs.competitionsetup.viewmodel.*;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

    @InjectMocks
    private CompetitionSetupServiceImpl service;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testPopulateCompetitionSectionModelAttributesNoMatchingFormPopulator() {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionCode("code")
                .withName("name")
                .withSetupComplete(false)
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(InitialDetailsModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.INITIAL_DETAILS);
        when(matchingPopulator.populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class)))
                .thenReturn(new InitialDetailsViewModel(getBasicGeneralSetupView(CompetitionSetupSection.INITIAL_DETAILS, competition),
                        emptyList(), emptyList(), emptyList(), emptyList(), emptyList()));
        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, section);

        verifyCommonModelAttributes(viewModel, competition, section);
        assertEquals("section-initial", viewModel.getGeneral().getCurrentSectionFragment());
    }

    @Test
    public void testPopulateCompetitionSectionModelAttributesEligibility() {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withSetupComplete(false)
                .withName("name")
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.ELIGIBILITY);
        when(matchingPopulator.populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class)))
                .thenReturn(new EligibilityViewModel(getBasicGeneralSetupView(CompetitionSetupSection.ELIGIBILITY, competition), new ResearchParticipationAmount[]{},
                new CollaborationLevel[]{}, emptyList(), "", emptyList(), ""));
        CompetitionSetupSectionModelPopulator notMatchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(notMatchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.MILESTONES);

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator, notMatchingPopulator));

        CompetitionSetupSection section = CompetitionSetupSection.ELIGIBILITY;

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, section);

        verifyCommonModelAttributes(viewModel, competition, section);
        assertEquals("section-eligibility", viewModel.getGeneral().getCurrentSectionFragment());

        verify(matchingPopulator).populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class));
        verify(notMatchingPopulator, never()).populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class));
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
    }

    @Test
    public void testGetSectionFormData() {
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

        CompetitionSetupForm result = service.getSectionFormData(competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);

        assertEquals(matchingForm, result);
        verify(matchingPopulator).populateForm(competitionResource);
        verify(otherPopulator, never()).populateForm(competitionResource);
    }

    @Test
    public void testSaveSection() {
        CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
        CompetitionResource competitionResource = newCompetitionResource()
                .withSectionSetupStatus(asMap(
                        CompetitionSetupSection.INITIAL_DETAILS, true,
                        CompetitionSetupSection.ADDITIONAL_INFO, false
                ))
                .build();

        CompetitionSetupSectionSaver matchingSaver = mock(CompetitionSetupSectionSaver.class);
        when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
        when(matchingSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(true);

        CompetitionSetupSectionSaver otherSaver = mock(CompetitionSetupSectionSaver.class);
        when(otherSaver.sectionToSave()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
        when(otherSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(false);

        when(matchingSaver.saveSection(competitionResource, competitionSetupForm)).thenReturn(serviceSuccess());

        service.setCompetitionSetupSectionSavers(asList(matchingSaver, otherSaver));

        service.saveCompetitionSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);

        verify(matchingSaver).saveSection(competitionResource, competitionSetupForm);
        verify(otherSaver, never()).saveSection(competitionResource, competitionSetupForm);
    }

    @Test(expected = IllegalStateException.class)
    public void autoSaveCompetitionSetupSection_initialDetailsMustBeComplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionSetupSection section = CompetitionSetupSection.ADDITIONAL_INFO;
        String fieldName = "testField";
        String value = "testValue";
        Optional<Long> objectId = Optional.of(1L);

        service.autoSaveCompetitionSetupSection(competition, section, fieldName, value, objectId);
    }

    @Test(expected = IllegalStateException.class)
    public void autoSaveCompetitionSetupSubsection_initialDetailsMustBeComplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        CompetitionSetupSubsection subsection = CompetitionSetupSubsection.APPLICATION_DETAILS;
        String fieldName = "testField";
        String value = "testValue";
        Optional<Long> objectId = Optional.of(1L);

        service.autoSaveCompetitionSetupSubsection(competition, section, subsection, fieldName, value, objectId);
    }

    @Test(expected = IllegalStateException.class)
    public void saveCompetitionSetupSection_initialDetailsMustBeComplete() throws Exception {
        CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionSetupSection section = CompetitionSetupSection.ADDITIONAL_INFO;

        service.saveCompetitionSetupSection(competitionSetupForm, competition, section);
    }


    @Test(expected = IllegalStateException.class)
    public void saveCompetitionSetupSubsection_initialDetailsMustBeComplete() throws Exception {
        CompetitionSetupForm competitionSetupForm = new ApplicationDetailsForm();
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        CompetitionSetupSubsection subsection = CompetitionSetupSubsection.APPLICATION_DETAILS;

        service.saveCompetitionSetupSubsection(competitionSetupForm, competition, section, subsection);
    }

    @Test
    public void testIsCompetitionReadyToOpen() {
        Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
        testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ASSESSORS, Boolean.FALSE);
        testSectionStatus.put(CompetitionSetupSection.CONTENT, Boolean.TRUE);

        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withStartDate(ZonedDateTime.now().plusDays(1)).build();
        competitionResource.setSectionSetupStatus(testSectionStatus);

        assertTrue(service.isCompetitionReadyToOpen(competitionResource));
    }

    @Test
    public void testIsCompetitionReadyToOpenFailure() {
        Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
        testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.FALSE);
        testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);

        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withStartDate(ZonedDateTime.now().plusDays(1)).build();
        competitionResource.setSectionSetupStatus(testSectionStatus);

        assertFalse(service.isCompetitionReadyToOpen(competitionResource));
    }

    @Test
    public void testSetCompetitionAsReadyToOpenWhenReadyToOpen() {
        CompetitionResource competitionResource = newCompetitionResource().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
        service.setCompetitionAsReadyToOpen(2L);
        assertEquals(competitionResource.getCompetitionStatus(), CompetitionStatus.READY_TO_OPEN);

    }

    @Test
    public void testSetCompetitionAsReadyToOpenSuccess() {
        long id = 2L;
        Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
        testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ASSESSORS, Boolean.FALSE);
        testSectionStatus.put(CompetitionSetupSection.CONTENT, Boolean.TRUE);
        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();
        competitionResource.setSectionSetupStatus(testSectionStatus);

        when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
        service.setCompetitionAsReadyToOpen(id);
        verify(competitionService).markAsSetup(id);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetCompetitionAsReadyToOpenFail() {
        Map<CompetitionSetupSection, Boolean> testSectionStatus = new HashMap<>();
        testSectionStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.ADDITIONAL_INFO, Boolean.FALSE);
        testSectionStatus.put(CompetitionSetupSection.ELIGIBILITY, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.MILESTONES, Boolean.TRUE);
        testSectionStatus.put(CompetitionSetupSection.APPLICATION_FORM, Boolean.TRUE);
        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();
        competitionResource.setSectionSetupStatus(testSectionStatus);

        when(competitionService.getById(any(Long.class))).thenReturn(competitionResource);
        service.setCompetitionAsReadyToOpen(2L);
        verify(competitionService.getById(any(Long.class)));
        verifyNoMoreInteractions(competitionResource);
    }

    @Test
    public void testPopulateModel() {
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

        CompetitionSetupSection competitionSetupSection = CompetitionSetupSection.ADDITIONAL_INFO;

        CompetitionResource competition = newCompetitionResource()
                .withSetupComplete(true)
                .withStartDate(yesterday)
                .withFundersPanelDate(yesterday)
                .build();

        CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
        when(matchingPopulator.sectionToPopulateModel()).thenReturn(competitionSetupSection);
        when(matchingPopulator.populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class)))
                .thenReturn(new AdditionalModelViewModel(getBasicGeneralSetupView(competitionSetupSection, competition)));

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, competitionSetupSection);

        assertEquals(false, viewModel.getGeneral().getState().isPreventEdit());
        assertEquals(false, viewModel.getGeneral().getState().isSetupAndLive());
        assertEquals(false, viewModel.getGeneral().getState().isSetupComplete());
    }

    @Test
    public void testPopulateModel_competitionNotSetupAndLive() {
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
        when(matchingPopulator.populateModel(any(GeneralSetupViewModel.class), any(CompetitionResource.class)))
                .thenReturn(new AdditionalModelViewModel(getBasicGeneralSetupView(competitionSetupSection, competition)));

        service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator));

        CompetitionSetupViewModel viewModel = service.populateCompetitionSectionModelAttributes(competition, competitionSetupSection);

        assertEquals(false, viewModel.getGeneral().getState().isPreventEdit());
        assertEquals(false, viewModel.getGeneral().getState().isSetupAndLive());
        assertEquals(false, viewModel.getGeneral().getState().isSetupComplete());
    }

    @Test
    public void autoSaveCompetitionSetupSection_restrictedField() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withSectionSetupStatus(asMap(
                        CompetitionSetupSection.INITIAL_DETAILS, true,
                        CompetitionSetupSection.ADDITIONAL_INFO, false)).build();
        CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        String[] restrictedFieldNames = new String[]{"competitionTypeId", "openingDate"};
        String[] unrestrictedFieldNames = new String[]{"title", "innovationSectorCategoryId",
                "autosaveInnovationAreaIds", "innovationLeadUserId", "executiveUserId"};
        String value = "testValue";
        Optional<Long> objectId = Optional.empty();
        CompetitionSetupForm form = new InitialDetailsForm();

        CompetitionSetupSectionSaver saver = mock(CompetitionSetupSectionSaver.class);
        CompetitionSetupFormPopulator populator = mock(CompetitionSetupFormPopulator.class);

        when(saver.sectionToSave()).thenReturn(CompetitionSetupSection.INITIAL_DETAILS);
        when(populator.sectionToFill()).thenReturn(CompetitionSetupSection.INITIAL_DETAILS);
        when(populator.populateForm(competition)).thenReturn(form);

        service.setCompetitionSetupSectionSavers(singletonList(saver));
        service.setCompetitionSetupFormPopulators(singletonList(populator));

        for (String fieldName : restrictedFieldNames) {
            try {
                service.autoSaveCompetitionSetupSection(competition, section, fieldName, value, objectId);
                fail("Expected IllegalStateException attempting to autosave restricted field: " + fieldName);
            } catch (IllegalStateException ignored) {

            }
        }

        for (String fieldName : unrestrictedFieldNames) {
            service.autoSaveCompetitionSetupSection(competition, section, fieldName, value, objectId);
            verify(saver).autoSaveSectionField(competition, form, fieldName, value, objectId);
        }
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionSetupSection section, CompetitionResource competition) {
        GeneralSetupViewModel generalSetupView = new GeneralSetupViewModel(Boolean.TRUE, competition, section, CompetitionSetupSection.values(), Boolean.FALSE);
        generalSetupView.setCurrentSectionFragment("section-" + section.getPath());
        generalSetupView.setState(new CompetitionStateSetupViewModel(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));

        return generalSetupView;
    }
}
