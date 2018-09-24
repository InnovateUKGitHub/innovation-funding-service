package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupStakeholderController;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.documentation.InviteUserResourceDocs;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

public class CompetitionSetupStakeholderControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupStakeholderController> {

    private InviteUserResource inviteUserResource;

    @Mock
    private CompetitionSetupStakeholderService competitionSetupStakeholderService;

    @Override
    protected CompetitionSetupStakeholderController supplyControllerUnderTest() {
        return new CompetitionSetupStakeholderController();
    }

    @Before
    public void setUp() {
        UserResource invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("Rayon")
                .withLastName("Kevin")
                .withEmail("Rayon.Kevin@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser);
    }

    @Test
    public void inviteStakeholder() throws Exception {

        long competitionId = 1L;

        when(competitionSetupStakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId)).thenReturn(serviceSuccess());


        mockMvc.perform(post("/competition/setup/{competitionId}/stakeholder/invite", competitionId)
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk())
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the Competition to which the Stakeholder is being invited")
                        ),
                        requestFields(InviteUserResourceDocs.inviteUserResourceFields)
                ));

        verify(competitionSetupStakeholderService).inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId);
    }
}


