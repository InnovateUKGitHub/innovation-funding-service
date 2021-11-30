package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.CompetitionParticipantController;
import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionParticipantResourceDocs.competitionParticipantResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionParticipantControllerDocumentation extends BaseControllerMockMVCTest<CompetitionParticipantController> {

    @Mock
    private CompetitionParticipantService competitionParticipantServiceMock;

    @Override
    protected CompetitionParticipantController supplyControllerUnderTest() {
        return new CompetitionParticipantController();
    }

    @Test
    public void getParticipants() throws Exception {
        Long userId = 1L;
        CompetitionParticipantRoleResource role = CompetitionParticipantRoleResource.ASSESSOR;
        ParticipantStatusResource status = ParticipantStatusResource.ACCEPTED;

        List<CompetitionParticipantResource> competitionParticipants = competitionParticipantResourceBuilder.build(2);

        when(competitionParticipantServiceMock.getCompetitionAssessors(userId)).thenReturn(serviceSuccess(competitionParticipants));

        mockMvc.perform(get("/competitionparticipant/user/{userId}", userId, status)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
