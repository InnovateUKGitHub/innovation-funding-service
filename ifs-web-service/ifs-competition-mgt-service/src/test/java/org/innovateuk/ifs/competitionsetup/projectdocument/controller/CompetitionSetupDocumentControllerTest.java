package org.innovateuk.ifs.competitionsetup.projectdocument.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.ProjectDocumentForm;
import org.innovateuk.ifs.competitionsetup.projectdocument.viewmodel.ProjectDocumentViewModel;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileTypeRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Class for testing public functions of {@link CompetitionSetupDocumentController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupDocumentControllerTest extends BaseControllerMockMVCTest<CompetitionSetupDocumentController> {
    private static final Long COMPETITION_ID = 12L;
    private static final String URL_PREFIX = format("/competition/setup/%d/section/project-document", COMPETITION_ID);

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupDocumentRestService competitionSetupDocumentRestService;

    @Mock
    private FileTypeRestService fileTypeRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected CompetitionSetupDocumentController supplyControllerUnderTest() { return new CompetitionSetupDocumentController(); }

    @Before
    public void setUp() {
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
    }

    @Test
    public void projectDocumentLandingPageWhenNonIfsCompetition() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withNonIfs(true)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/non-ifs-competition/setup/" + COMPETITION_ID))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService, never()).populateCompetitionSectionModelAttributes(any(), any());
    }

    @Test
    public void projectDocumentLandingPageWhenInitialDetailsNotCompleteAndNotTouched() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withNonIfs(false)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(false);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService, never()).populateCompetitionSectionModelAttributes(any(), any());
    }

    @Test
    public void projectDocumentLandingPage() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        assertEquals(viewModel, model.get("model"));
    }

    @Test
    public void saveProjectDocumentLandingPageFailsWhenNoDocumentsSelected() throws Exception {

        List<CompetitionDocumentResource> projectDocuments = newCompetitionDocumentResource()
                .withTitle("Title")
                .withEditable(true)
                .withEnabled(true)
                .build(2);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withProjectDocument(projectDocuments)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);

        mockMvc.perform(post(URL_PREFIX + "/landing-page"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupDocumentRestService, never()).save(anyList());
    }

    @Test
    public void viewAddProjectDocument() throws Exception {
        List<CompetitionDocumentResource> projectDocuments = newCompetitionDocumentResource()
                .withTitle("Title")
                .withEditable(true)
                .withEnabled(true)
                .build(2);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withProjectDocument(projectDocuments)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/save-project-document"))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        assertEquals(new ProjectDocumentForm(true, true), model.get("form"));
        assertEquals(viewModel, model.get("model"));
    }

    @Test
    public void saveProjectDocumentWhenErrorsOnForm() throws Exception {

        String title = "Risk Document";

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);

        ModelMap model = mockMvc.perform(post(URL_PREFIX + "/save")
                                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                            .param("title", title)
                                            .param("editable", "true")
                                            .param("enabled", "true")
                                        )
                                .andExpect(status().isOk())
                                .andExpect(view().name("competition/setup/save-project-document"))
                                .andReturn().getModelAndView().getModelMap();

        ProjectDocumentForm expectedForm = new ProjectDocumentForm(null, title, null, true, true);
        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        verify(competitionSetupDocumentRestService, never()).save(any(CompetitionDocumentResource.class));
        assertEquals(expectedForm, model.get("form"));

    }

    @Test
    public void saveProjectDocumentWhenErrorWhilstSaving() throws Exception {

        String title = "Risk Document";
        String guidance = "Risk Document guidance";

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);
        when(competitionSetupDocumentRestService.save(any(CompetitionDocumentResource.class))).thenReturn(restFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

        ModelMap model = mockMvc.perform(post(URL_PREFIX + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("guidance", guidance)
                .param("editable", "true")
                .param("enabled", "true")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/save-project-document"))
                .andReturn().getModelAndView().getModelMap();

        ProjectDocumentForm expectedForm = new ProjectDocumentForm(null, title, guidance, true, true);
        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        verify(competitionSetupDocumentRestService).save(any(CompetitionDocumentResource.class));
        assertEquals(expectedForm, model.get("form"));
    }

    @Test
    public void saveProjectDocumentSuccess() throws Exception {

        String title = "Risk Document";
        String guidance = "Risk Document guidance";

        FileTypeResource fileTypeResource = new FileTypeResource();

        CompetitionDocumentResource expectedCompetitionDocumentResource = new CompetitionDocumentResource(COMPETITION_ID, title, guidance,
                true, true, singletonList(fileTypeResource.getId()));

        when(fileTypeRestService.findByName("PDF")).thenReturn(restSuccess(fileTypeResource));
        when(competitionSetupDocumentRestService.save(expectedCompetitionDocumentResource)).thenReturn(restSuccess(expectedCompetitionDocumentResource));


        mockMvc.perform(post(URL_PREFIX + "/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("guidance", guidance)
                .param("editable", "true")
                .param("enabled", "true")
                .param("pdf", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/competition/setup/%d/section/project-document/landing-page", COMPETITION_ID)));

        verify(competitionSetupDocumentRestService).save(expectedCompetitionDocumentResource);
    }

    @Test
    public void viewEditProjectDocument() throws Exception {

        long projectDocumentId = 2L;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();
        long fileTypeId = 1L;

        CompetitionDocumentResource competitionDocumentResource = newCompetitionDocumentResource()
                .withId(projectDocumentId)
                .withTitle("Title")
                .withGuidance("Guidance")
                .withEditable(true)
                .withEnabled(true)
                .withFileType(singletonList(fileTypeId))
                .build();

        FileTypeResource fileTypeResource = new FileTypeResource();
        fileTypeResource.setName("PDF");


        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);
        when(competitionSetupDocumentRestService.findOne(projectDocumentId)).thenReturn(restSuccess(competitionDocumentResource));
        when(fileTypeRestService.findOne(fileTypeId)).thenReturn(restSuccess(fileTypeResource));

        ProjectDocumentForm expectedProjectDocumentForm = new ProjectDocumentForm(projectDocumentId, "Title", "Guidance", true, true);
        expectedProjectDocumentForm.setPdf(true);
        expectedProjectDocumentForm.setSpreadsheet(false);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/" + projectDocumentId + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/save-project-document"))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        assertEquals(expectedProjectDocumentForm, model.get("form"));
        assertEquals(viewModel, model.get("model"));
    }

    @Test
    public void deleteProjectDocument() throws Exception {

        long projectDocumentId = 2L;

        when(competitionSetupDocumentRestService.delete(projectDocumentId)).thenReturn(restSuccess());

        mockMvc.perform(post(URL_PREFIX + "/{projectDocumentId}/delete", projectDocumentId)
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/competition/setup/%d/section/project-document/landing-page", COMPETITION_ID)));

        verify(competitionSetupDocumentRestService).delete(projectDocumentId);
    }
}

