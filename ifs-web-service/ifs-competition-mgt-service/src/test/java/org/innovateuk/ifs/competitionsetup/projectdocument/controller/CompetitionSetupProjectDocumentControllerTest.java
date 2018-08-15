package org.innovateuk.ifs.competitionsetup.projectdocument.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.builder.ProjectDocumentResourceBuilder;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupProjectDocumentRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.projectdocument.form.LandingPageForm;
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
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Class for testing public functions of {@link CompetitionSetupProjectDocumentController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupProjectDocumentControllerTest extends BaseControllerMockMVCTest<CompetitionSetupProjectDocumentController> {
    private static final Long COMPETITION_ID = 12L;
    private static final String URL_PREFIX = format("/competition/setup/%d/section/project-document", COMPETITION_ID);

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupProjectDocumentRestService competitionSetupProjectDocumentRestService;

    @Mock
    private FileTypeRestService fileTypeRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected CompetitionSetupProjectDocumentController supplyControllerUnderTest() { return new CompetitionSetupProjectDocumentController(); }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);
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
        assertNull(model.get("landingPageForm"));
    }

    @Test
    public void projectDocumentLandingPageWhenInitialDetailsNotCompleteAndNotTouched() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withNonIfs(false)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(false);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/landing-page"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService, never()).populateCompetitionSectionModelAttributes(any(), any());
        assertNull(model.get("landingPageForm"));
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
        assertEquals(new LandingPageForm(), model.get("landingPageForm"));
        assertEquals(viewModel, model.get("model"));
    }

    @Test
    public void viewAddProjectDocument() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        ProjectDocumentViewModel viewModel = new ProjectDocumentViewModel(null);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT)).thenReturn(viewModel);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/save-project-document"))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        assertEquals(new LandingPageForm(), model.get("landingPageForm"));
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
        verify(competitionSetupProjectDocumentRestService, never()).save(any(ProjectDocumentResource.class));
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
        when(competitionSetupProjectDocumentRestService.save(any(ProjectDocumentResource.class))).thenReturn(restFailure(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

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
        verify(competitionSetupProjectDocumentRestService).save(any(ProjectDocumentResource.class));
        assertEquals(expectedForm, model.get("form"));
    }

    @Test
    public void saveProjectDocumentSuccess() throws Exception {

        String title = "Risk Document";
        String guidance = "Risk Document guidance";

        FileTypeResource fileTypeResource = new FileTypeResource();

        ProjectDocumentResource expectedProjectDocumentResource = new ProjectDocumentResource(COMPETITION_ID, title, guidance,
                true, true, singletonList(fileTypeResource.getId()));

        when(fileTypeRestService.findByName("PDF")).thenReturn(restSuccess(fileTypeResource));
        when(competitionSetupProjectDocumentRestService.save(expectedProjectDocumentResource)).thenReturn(restSuccess(expectedProjectDocumentResource));


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

        verify(competitionSetupProjectDocumentRestService).save(expectedProjectDocumentResource);
    }

    @Test
    public void viewEditProjectDocument() throws Exception {

        long projectDocumentId = 2L;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();
        long fileTypeId = 1L;

        ProjectDocumentResource projectDocumentResource = ProjectDocumentResourceBuilder.newProjectDocumentResource()
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
        when(competitionSetupProjectDocumentRestService.findOne(projectDocumentId)).thenReturn(restSuccess(projectDocumentResource));
        when(fileTypeRestService.findOne(fileTypeId)).thenReturn(restSuccess(fileTypeResource));

        ProjectDocumentForm expectedProjectDocumentForm = new ProjectDocumentForm(projectDocumentId, "Title", "Guidance", true, true);
        expectedProjectDocumentForm.setPdf(true);
        expectedProjectDocumentForm.setSpreadsheet(false);

        ModelMap model = mockMvc.perform(get(URL_PREFIX + "/" + projectDocumentId + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/save-project-document"))
                .andReturn().getModelAndView().getModelMap();

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(competitionResource, PROJECT_DOCUMENT);
        assertEquals(new LandingPageForm(), model.get("landingPageForm"));
        assertEquals(expectedProjectDocumentForm, model.get("form"));
        assertEquals(viewModel, model.get("model"));
    }

    @Test
    public void deleteProjectDocument() throws Exception {

        long projectDocumentId = 2L;

        when(competitionSetupProjectDocumentRestService.delete(projectDocumentId)).thenReturn(restSuccess());

        mockMvc.perform(post(URL_PREFIX + "/{projectDocumentId}/delete", projectDocumentId)
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/competition/setup/%d/section/project-document/landing-page", COMPETITION_ID)));

        verify(competitionSetupProjectDocumentRestService).delete(projectDocumentId);
    }
}

