package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.UserDocs.userResourceFields;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class UserControllerDocumentation extends BaseControllerMockMVCTest<UserController> {

    private RestDocumentationResultHandler document;

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Before
    public void setUp() throws Exception {
        this.document = document("user/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void sendEmailVerificationNotification() throws Exception {
        final String emailAddress = "sample@me.com";

        final UserResource userResource = newUserResource().build();

        when(userServiceMock.findInactiveByEmail(emailAddress)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, empty())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/sendEmailVerificationNotification/{emailAddress}/", emailAddress))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("emailAddress").description("E-mail address of the user who a verification link should be sent to by e-mail")
                        )
                ));
    }

    @Test
    public void createUser() throws Exception {
        final Long organisationId = 9999L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createApplicantUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, empty())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}", organisationId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation who the user is the lead applicant for")
                        ),
                        requestFields(userResourceFields),
                        responseFields(userResourceFields)
                ));
    }

    @Test
    public void createUserWithCompetitionId() throws Exception {
        final Long organisationId = 9999L;
        final Long competitionId = 8888L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createApplicantUser(organisationId, of(competitionId), userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, of(competitionId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}/{competitionId}", organisationId, competitionId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation who the user is the lead applicant for"),
                                parameterWithName("competitionId").description("Identifier of the competition that the user is applying for")
                        ),
                        requestFields(userResourceFields),
                        responseFields(userResourceFields)
                ));
    }

}