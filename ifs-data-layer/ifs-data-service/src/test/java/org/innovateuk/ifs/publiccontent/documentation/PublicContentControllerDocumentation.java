package org.innovateuk.ifs.publiccontent.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.controller.PublicContentController;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.PublicContentResourceDocs.publicContentResourceBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicContentControllerDocumentation extends BaseControllerMockMVCTest<PublicContentController> {
    @Mock
    PublicContentService publicContentService;

    @Override
    protected PublicContentController supplyControllerUnderTest() {
        return new PublicContentController();
    }

    @Test
    public void findByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(publicContentService.findByCompetitionId(competitionId)).thenReturn(serviceSuccess(publicContentResourceBuilder.build()));

        mockMvc.perform(get("/public-content/find-by-competition-id/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void publishByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(publicContentService.publishByCompetitionId(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/publish-by-competition-id/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSection() throws Exception {
        PublicContentResource resource = publicContentResourceBuilder.build();

        when(publicContentService.updateSection(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/update-section/{section}/{id}", PublicContentSectionType.DATES.name(), resource.getId(), "json")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk());

    }

    @Test
    public void markSectionAsComplete() throws Exception {
        PublicContentResource resource = publicContentResourceBuilder.build();

        when(publicContentService.markSectionAsComplete(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/mark-section-as-complete/{section}/{id}", PublicContentSectionType.DATES.name(), resource.getId(), "json")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk());

    }

}
