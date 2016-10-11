package com.worth.ifs.assessment.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.controller.CompetitionInviteController;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.CompetitionInviteDocs.*;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionInviteDocumentation extends BaseControllerMockMVCTest<CompetitionInviteController> {

    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Before
    public void setup(){
        this.document = document("competitioninvite/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getInvite() throws Exception {
        String hash = "invitehash";
        CompetitionInviteResource competitionInviteResource = competitionInviteResourceBuilder.build();

        when(competitionInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        mockMvc.perform(get("/competitioninvite/getInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being requested")
                        ),
                        responseFields(competitionInviteFields)
                ));
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "invitehash";
        CompetitionInviteResource competitionInviteResource = competitionInviteResourceBuilder.build();

        when(competitionInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        mockMvc.perform(post("/competitioninvite/openInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being opened")
                        ),
                        responseFields(competitionInviteFields)
                ));
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "invitehash";
        UserResource user = newUserResource().build();

        login(user);

        when(competitionInviteServiceMock.acceptInvite(hash, user)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/acceptInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        )
                ));
    }

    @Test
    public void rejectInvite() throws Exception {
        String hash = "invitehash";
        CompetitionRejectionResource compRejection = competitionInviteResource;

        when(competitionInviteServiceMock.rejectInvite(hash, compRejection.getRejectReason(), ofNullable(compRejection.getRejectComment()))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/rejectInvite/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(compRejection)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        requestFields(competitionRejectionFields),
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being rejected")
                        )
                ));
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "invitehash";

        when(competitionInviteServiceMock.checkExistingUser(hash)).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(get("/competitioninvite/checkExistingUser/{hash}", hash))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being checked")
                        )
                ));
    }
}
