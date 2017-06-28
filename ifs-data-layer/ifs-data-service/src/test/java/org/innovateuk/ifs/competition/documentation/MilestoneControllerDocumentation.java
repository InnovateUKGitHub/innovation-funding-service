package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.MilestoneController;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.documentation.MilestoneResourceDocs.milestoneResourceFields;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MilestoneControllerDocumentation extends BaseControllerMockMVCTest<MilestoneController> {

    @Mock
    private MilestoneService milestoneService;

    private RestDocumentationResultHandler document;

    @Override
    protected MilestoneController supplyControllerUnderTest() {
        return new MilestoneController();
    }

    @Before
    public void setup() {
        this.document = document("milestone/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getAllPublicMilestonesByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(milestoneService.getAllPublicMilestonesByCompetitionId(competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build(1)));

        mockMvc.perform(get("/milestone/{id}/public", competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the competition where milestones should be from retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of milestones for all users")
                        )
                ));
    }

    @Test
    public void getAllMilestonesByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(milestoneService.getAllMilestonesByCompetitionId(competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build(1)));

        mockMvc.perform(get("/milestone/{id}/", competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the competition where milestones should be from retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of milestones for the authenticated user")
                        )
                ));
    }

    @Test
    public void getMilestoneByTypeAndCompetitionId() throws Exception {
        Long competitionId = 2L;
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build()));

        mockMvc.perform(get("/milestone/{competitionId}/getByType?type=" + MilestoneType.OPEN_DATE, competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition where milestone should be from retrieved")
                        ),
                        requestParameters(
                                parameterWithName("type").description("milestone type that is being requested")
                        ),
                        responseFields(milestoneResourceFields)
                ));
    }

    @Test
    public void create() throws Exception {
        Long competitionId = 2L;
        when(milestoneService.create(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build()));

        mockMvc.perform(post("/milestone/{competitionId}?type=" + MilestoneType.OPEN_DATE, competitionId))
                .andExpect(status().isCreated())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("id of the competition where this milestone should be added")
                        ),
                        requestParameters(
                                parameterWithName("type").description("milestone type that is being created")
                        ),
                        responseFields(milestoneResourceFields)
                ));
    }

    @Test
    public void saveMilestones() throws Exception {
        List<MilestoneResource> milestoneResources = newMilestoneResource().build(1);

        when(milestoneService.updateMilestones(anyList()))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/milestone/many")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(milestoneResources)))
            .andExpect(status().isOk())
            .andDo(this.document.snippets(
                requestFields(
                        fieldWithPath("[]").description("list of milestones that should be saved")
                )
            ));
    }

    @Test
    public void saveMilestone() throws Exception {
        MilestoneResource milestoneResource = newMilestoneResource().build();
        when(milestoneService.updateMilestone(any(MilestoneResource.class)))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/milestone/")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(milestoneResource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        requestFields(milestoneResourceFields)
                ));
    }
}
