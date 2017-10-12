package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentMenuPopulator;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link PublicContentMenuController}
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicContentMenuControllerTest extends BaseControllerMockMVCTest<PublicContentMenuController> {

    private static final Long COMPETITION_ID = Long.valueOf(12);
    private static final String URL_PREFIX = "/competition/setup/public-content";
    private static final CompetitionResource defaultCompetition = newCompetitionResource()
            .build();

    private static final String WEB_BASE_URL = "https://environment";

    @Mock
    private PublicContentMenuPopulator publicContentMenuPopulator;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Override
    protected PublicContentMenuController supplyControllerUnderTest() {
        return new PublicContentMenuController();
    }

    @Test
    public void testGetPublicContentMenu() throws Exception {
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(defaultCompetition));
        when(publicContentMenuPopulator.populate(COMPETITION_ID, WEB_BASE_URL)).thenReturn(new PublicContentMenuViewModel());
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/public-content-menu"));
    }

    @Test
    public void testPublishPublicContentSuccess() throws Exception {
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(defaultCompetition));
        when(publicContentService.publishByCompetitionId(COMPETITION_ID)).thenReturn(serviceSuccess());
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/public-content/" + COMPETITION_ID));

        verify(publicContentService).publishByCompetitionId(COMPETITION_ID);
    }

    @Test
    public void testPublishPublicContentFailure() throws Exception {
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(defaultCompetition));
        when(publicContentService.publishByCompetitionId(COMPETITION_ID)).thenReturn(serviceFailure(PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH));
        when(publicContentMenuPopulator.populate(COMPETITION_ID, WEB_BASE_URL)).thenReturn(new PublicContentMenuViewModel());
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/public-content-menu"))
                .andExpect(model().attributeHasFieldErrorCode("form", "", "PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH"));

        verify(publicContentService).publishByCompetitionId(COMPETITION_ID);
    }
}