package org.innovateuk.ifs.assessment.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.CompetitionInviteController;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.*;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;
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
    public void getCreatedInvite() throws Exception {
        long inviteId = 1L;
        AssessorInviteToSendResource resource = assessorInviteToSendResourceBuilder.build();

        when(competitionInviteServiceMock.getCreatedInvite(inviteId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competitioninvite/getCreated/{inviteId}", inviteId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("inviteId").description("Id of the created invite being requested")
                        ),
                        responseFields(assessorToSendFields)
                ));
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

    @Test
    public void sendInvite() throws Exception {
        long inviteId = 1L;
        EmailContent content = newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain text")
                .withHtmlText("<html>html text</htm>")
                .build();

        AssessorInviteToSendResource resource = assessorInviteToSendResourceBuilder.build();

        when(competitionInviteServiceMock.sendInvite(inviteId, content)).thenReturn(serviceSuccess(resource));

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/competitioninvite/sendInvite/{inviteId}", inviteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(content)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("inviteId").description("Id of the created invite being sent")
                        ),
                        responseFields(assessorToSendFields)
                ));
    }
}
