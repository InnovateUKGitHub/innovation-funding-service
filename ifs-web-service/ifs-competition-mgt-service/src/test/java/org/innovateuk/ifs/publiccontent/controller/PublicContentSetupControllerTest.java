package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.publiccontent.PublicContentSetupController;
import org.innovateuk.ifs.publiccontent.populator.PublicContentMenuPopulator;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link PublicContentSetupController}
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicContentSetupControllerTest extends BaseControllerMockMVCTest<PublicContentSetupController> {

    private static final Long COMPETITION_ID = Long.valueOf(12);
    private static final String URL_PREFIX = "/competition/setup/public-content";

    @Mock
    private PublicContentMenuPopulator publicContentMenuPopulator;

    @Mock
    private PublicContentService publicContentService;


    @Override
    protected PublicContentSetupController supplyControllerUnderTest() {
        return new PublicContentSetupController();
    }

    @Test
    public void testGetPublicContentMenu() throws Exception {
        when(publicContentMenuPopulator.populate(COMPETITION_ID)).thenReturn(serviceSuccess(new PublicContentMenuViewModel()));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/public-content-menu"));
    }

    @Test
    public void testPublishPublicContentSuccess() throws Exception {
        when(publicContentService.publishByCompetitionId(COMPETITION_ID)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/public-content/" + COMPETITION_ID));

        verify(publicContentService).publishByCompetitionId(COMPETITION_ID);
    }

    @Test
    public void testPublishPublicContentFailure() throws Exception {
        when(publicContentService.publishByCompetitionId(COMPETITION_ID)).thenReturn(serviceFailure(PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH));
        when(publicContentMenuPopulator.populate(COMPETITION_ID)).thenReturn(serviceSuccess(new PublicContentMenuViewModel()));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/public-content-menu"))
                .andExpect(model().attributeHasFieldErrorCode("form", "", "PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH"));

        verify(publicContentService).publishByCompetitionId(COMPETITION_ID);
    }
}