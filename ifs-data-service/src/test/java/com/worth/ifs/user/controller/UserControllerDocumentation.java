package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileAddressResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AffiliationDocs.affiliationResourceBuilder;
import static com.worth.ifs.documentation.AffiliationDocs.affiliationResourceFields;
import static com.worth.ifs.documentation.ProfileAddressDocs.profileAddressResourceBuilder;
import static com.worth.ifs.documentation.ProfileAddressDocs.profileAddressResourceFields;
import static com.worth.ifs.documentation.ProfileSkillsDocs.profileSkillsResourceBuilder;
import static com.worth.ifs.documentation.ProfileSkillsDocs.profileSkillsResourceFields;
import static com.worth.ifs.documentation.UserDocs.userResourceFields;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_TECHNOLOGIST;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("emailAddress").description("E-mail address of the user who a verification link should be sent to by e-mail")
                        )
                ));
    }

    @Test
    public void createUser() throws Exception {
        final Long organisationId = 9999L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createOrganisationUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, empty())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}", organisationId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation who the user is the lead applicant for")
                        ),
                        requestFields(userResourceFields),
                        responseFields(userResourceFields)
                ));
    }

    @Test
    public void findByRole() throws Exception {

        final UserResource userResource = newUserResource().build();
        when(userServiceMock.findByProcessRole(eq(COMP_TECHNOLOGIST))).thenReturn(serviceSuccess(asList(userResource, userResource)));

        mockMvc.perform(get("/user/findByRole/{userRoleName}", COMP_TECHNOLOGIST.getName()))
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("userRoleName").description("The name of the role to get the users by.")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of users with the selected role")
                        )
                ));
    }

    @Test
    public void createUserWithCompetitionId() throws Exception {
        final Long organisationId = 9999L;
        final Long competitionId = 8888L;

        final UserResource userResource = newUserResource().build();
        when(registrationServiceMock.createOrganisationUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, of(competitionId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/{organisationId}/{competitionId}", organisationId, competitionId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userResource)))
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation who the user is the lead applicant for"),
                                parameterWithName("competitionId").description("Identifier of the competition that the user is applying for")
                        ),
                        requestFields(userResourceFields),
                        responseFields(userResourceFields)
                ));
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkills = profileSkillsResourceBuilder.build();

        when(userProfileServiceMock.getProfileSkills(userId)).thenReturn(serviceSuccess(profileSkills));

        mockMvc.perform(get("/user/id/{id}/getProfileSkills", userId))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile skills being requested")
                        ),
                        responseFields(profileSkillsResourceFields)
                ));
    }

    @Test
    public void updateProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkills = profileSkillsResourceBuilder.build();

        when(userProfileServiceMock.updateProfileSkills(userId, profileSkills)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(profileSkills)))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile skills for")
                        ),
                        requestFields(profileSkillsResourceFields)
                ));
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> responses = affiliationResourceBuilder.build(2);
        when(userProfileServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/user/id/{id}/getUserAffiliations", userId))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with affiliations being requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of affiliations belonging to the user")
                        ).andWithPrefix("[].", affiliationResourceFields)
                ));
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = affiliationResourceBuilder
                .build(2);
        when(userProfileServiceMock.updateUserAffiliations(userId, affiliations)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateUserAffiliations", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(affiliations)))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with affiliations being updated")
                        ),
                        requestFields(fieldWithPath("[]").description("List of affiliations belonging to the user"))
                                .andWithPrefix("[].", affiliationResourceFields)
                ));
    }

    @Test
    public void getProfileAddress() throws Exception {
        Long userId = 1L;
        ProfileAddressResource profileAddress = profileAddressResourceBuilder.build();

        when(userProfileServiceMock.getProfileAddress(userId)).thenReturn(serviceSuccess(profileAddress));

        mockMvc.perform(get("/user/id/{id}/getProfileAddress", userId))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile address being requested")
                        ),
                        responseFields(profileAddressResourceFields)
                ));
    }

    @Test
    public void updateProfileAddress() throws Exception {
        Long userId = 1L;
        ProfileAddressResource profileAddress = profileAddressResourceBuilder.build();

        when(userProfileServiceMock.updateProfileAddress(userId, profileAddress)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileAddress", userId)
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(profileAddress)))
                .andExpect(status().isOk())
                .andDo(this.document.document(
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile address for")
                        ),
                        requestFields(profileAddressResourceFields)
                ));
    }
}