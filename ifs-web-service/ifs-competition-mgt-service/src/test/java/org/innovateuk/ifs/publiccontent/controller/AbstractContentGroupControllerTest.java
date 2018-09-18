package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AbstractContentGroupControllerTest extends BaseControllerMockMVCTest<AbstractContentGroupControllerTest
        .TestContentGroupController> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private PublicContentFormSaver<TestPublicContentForm> formSaver;

    @Mock
    private PublicContentViewModelPopulator<TestPublicContentViewModel> modelPopulator;

    @Mock
    private PublicContentFormPopulator<TestPublicContentForm> formPopulator;

    private PublicContentSectionType publicContentSectionType = PublicContentSectionType.ELIGIBILITY;

    @Test
    public void saveAndUpload() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormToSave = new TestPublicContentForm();
        TestPublicContentForm expectedFormPopulated = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(competition.getId())).thenReturn(true);
        when(publicContentService.uploadFile(competition.getId(), publicContentSectionType,
                expectedFormToSave.getContentGroups())).thenReturn(serviceSuccess());
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(formSaver.save(expectedFormToSave, publicContent)).thenReturn(serviceSuccess());
        when(modelPopulator.populate(publicContent, false)).thenReturn(expectedViewModel);
        when(formPopulator.populate(publicContent)).thenReturn(expectedFormPopulated);

        mockMvc.perform(post("/competition/setup/public-content/test-content-group/{competitionId}/edit", competition
                .getId())
                .param("uploadFile", ""))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedFormPopulated))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("competition/public-content-form"));

        verify(formSaver, only()).save(expectedFormToSave, publicContent);
        verify(publicContentService).uploadFile(competition.getId(), publicContentSectionType, expectedFormToSave
                .getContentGroups());
    }

    @Test
    public void saveAndRemove() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormToSave = new TestPublicContentForm();
        TestPublicContentForm expectedFormPopulated = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(competition.getId())).thenReturn(true);
        when(publicContentService.removeFile(expectedFormToSave)).thenReturn(serviceSuccess());
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(formSaver.save(expectedFormToSave, publicContent)).thenReturn(serviceSuccess());
        when(modelPopulator.populate(publicContent, false)).thenReturn(expectedViewModel);
        when(formPopulator.populate(publicContent)).thenReturn(expectedFormPopulated);

        mockMvc.perform(post("/competition/setup/public-content/test-content-group/{competitionId}/edit", competition
                .getId())
                .param("removeFile", ""))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedFormPopulated))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("competition/public-content-form"));

        verify(formSaver, only()).save(expectedFormToSave, publicContent);
        verify(publicContentService).removeFile(expectedFormToSave);
    }

    @Override
    protected TestContentGroupController supplyControllerUnderTest() {
        return new TestContentGroupController();
    }

    @Controller
    @RequestMapping("/competition/setup/public-content/test-content-group")
    public class TestContentGroupController extends AbstractContentGroupController<TestPublicContentViewModel,
            TestPublicContentForm> {

        @Override
        protected PublicContentSectionType getType() {
            return publicContentSectionType;
        }

        @Override
        protected PublicContentViewModelPopulator<TestPublicContentViewModel> modelPopulator() {
            return modelPopulator;
        }

        @Override
        protected PublicContentFormPopulator<TestPublicContentForm> formPopulator() {
            return formPopulator;
        }

        @Override
        protected PublicContentFormSaver<TestPublicContentForm> formSaver() {
            return formSaver;
        }
    }
}