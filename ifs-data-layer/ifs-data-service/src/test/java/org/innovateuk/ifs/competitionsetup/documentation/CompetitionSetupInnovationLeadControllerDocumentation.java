package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupInnovationLeadController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupInnovationLeadControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupInnovationLeadController> {

    @Mock
    private CompetitionSetupInnovationLeadService competitionSetupInnovationLeadService;

    @Override
    protected CompetitionSetupInnovationLeadController supplyControllerUnderTest() {
        return new CompetitionSetupInnovationLeadController();
    }

    @Test
    public void findAvailableInnovationLeadsNotAssignedToCompetition() throws Exception {
        final long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionSetupInnovationLeadService.findInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/setup/{id}/innovation-leads", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation leads need to be found")
                        )
                ));
    }

    @Test
    public void findInnovationLeadsAddedToCompetition() throws Exception {
        final long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionSetupInnovationLeadService.findAddedInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/setup/{id}/innovation-leads/find-added", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation leads need to be found")
                        )
                ));
    }

    @Test
    public void addInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadService.addInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{id}/add-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation lead needs to be added"),
                                parameterWithName("innovationLeadUserId").description("The id of the innovation lead which is being added")
                        )
                ));

        verify(competitionSetupInnovationLeadService, only()).addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void removeInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadService.removeInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{id}/remove-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which innovation lead needs to be deleted"),
                                parameterWithName("innovationLeadUserId").description("The id of the innovation lead which is being deleted")
                        )
                ));

        verify(competitionSetupInnovationLeadService, only()).removeInnovationLead(competitionId, innovationLeadUserId);
    }
}
