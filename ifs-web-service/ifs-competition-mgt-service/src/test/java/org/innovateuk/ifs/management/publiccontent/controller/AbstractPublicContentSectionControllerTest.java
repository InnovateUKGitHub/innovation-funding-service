package org.innovateuk.ifs.management.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.publiccontent.controller.AbstractPublicContentSectionController;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.saver.PublicContentFormSaver;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType.DATES;
import static org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType.SUMMARY;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AbstractPublicContentSectionControllerTest extends
        BaseControllerMockMVCTest<AbstractPublicContentSectionControllerTest.TestPublicContentSectionControllerTest> {

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

    @Test
    public void readOnly() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormPopulated = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())).thenReturn(true);
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(modelPopulator.populate(publicContent, true)).thenReturn(expectedViewModel);
        when(formPopulator.populate(publicContent)).thenReturn(expectedFormPopulated);

        mockMvc.perform(get("/competition/setup/public-content/test-content-section/{competitionId}", competition
                .getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedFormPopulated))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("competition/public-content-form"));
    }

    @Test
    public void edit() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormPopulated = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())).thenReturn(true);
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(modelPopulator.populate(publicContent, false)).thenReturn(expectedViewModel);
        when(formPopulator.populate(publicContent)).thenReturn(expectedFormPopulated);

        mockMvc.perform(get("/competition/setup/public-content/test-content-section/{competitionId}/edit", competition
                .getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedFormPopulated))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("competition/public-content-form"));
    }

    @Test
    public void markAsComplete_DatesSection() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormToSave = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        PublicContentSectionResource publicContentSectionResource = newPublicContentSectionResource()
                .withType(DATES)
                .build();
        expectedViewModel.setSection(publicContentSectionResource);

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())).thenReturn(true);
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(modelPopulator.populate(publicContent, true)).thenReturn(expectedViewModel);
        when(formSaver.markAsComplete(expectedFormToSave, publicContent)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/public-content/test-content-section/{competitionId}/edit", competition
                .getId()))
                .andExpect(status().isFound())
                .andExpect(model().attribute("form", expectedFormToSave))
                .andExpect(view().name("redirect:/competition/setup/public-content/dates/" + competition.getId()));

        verify(formSaver, only()).markAsComplete(expectedFormToSave, publicContent);
    }

    @Test
    public void markAsComplete_SectionsOtherThanDates() throws Exception {
        CompetitionResource competition = newCompetitionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();

        TestPublicContentForm expectedFormToSave = new TestPublicContentForm();
        TestPublicContentViewModel expectedViewModel = new TestPublicContentViewModel();

        PublicContentSectionResource publicContentSectionResource = newPublicContentSectionResource()
                .withType(SUMMARY)
                .build();
        expectedViewModel.setSection(publicContentSectionResource);

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())).thenReturn(true);
        when(publicContentService.getCompetitionById(competition.getId())).thenReturn(publicContent);
        when(modelPopulator.populate(publicContent, true)).thenReturn(expectedViewModel);
        when(formSaver.markAsComplete(expectedFormToSave, publicContent)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/public-content/test-content-section/{competitionId}/edit", competition
                .getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedFormToSave))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("competition/public-content-form"));

        verify(formSaver, only()).markAsComplete(expectedFormToSave, publicContent);
    }

    @Override
    protected TestPublicContentSectionControllerTest supplyControllerUnderTest() {
        return new TestPublicContentSectionControllerTest();
    }

    @Controller
    @RequestMapping("/competition/setup/public-content/test-content-section")
    public class TestPublicContentSectionControllerTest extends
            AbstractPublicContentSectionController<TestPublicContentViewModel, TestPublicContentForm> {

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