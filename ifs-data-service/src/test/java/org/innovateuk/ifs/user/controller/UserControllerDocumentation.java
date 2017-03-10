package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationResourceBuilder;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationResourceFields;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceBuilder;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceFields;
import static org.innovateuk.ifs.documentation.ProfileSkillsDocs.*;
import static org.innovateuk.ifs.documentation.UserDocs.userResourceFields;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceBuilder;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceFields;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_TECHNOLOGIST;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocumentation extends BaseControllerMockMVCTest<UserController> {

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Test
    public void sendEmailVerificationNotification() throws Exception {
        final String emailAddress = "sample@me.com";

        final UserResource userResource = newUserResource().build();

        when(userServiceMock.findInactiveByEmail(emailAddress)).thenReturn(serviceSuccess(userResource));
        when(registrationServiceMock.sendUserVerificationEmail(userResource, empty())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/sendEmailVerificationNotification/{emailAddress}/", emailAddress))
                .andDo(document("user/{method-name}",
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
                .content(objectMapper.writeValueAsString(userResource)))
                .andDo(document("user/{method-name}",
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
        when(baseUserServiceMock.findByProcessRole(eq(COMP_TECHNOLOGIST))).thenReturn(serviceSuccess(asList(userResource, userResource)));

        mockMvc.perform(get("/user/findByRole/{userRoleName}", COMP_TECHNOLOGIST.getName()))
                .andDo(document("user/{method-name}",
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
                .content(objectMapper.writeValueAsString(userResource)))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation who the user is the lead applicant for"),
                                parameterWithName("competitionId").description("Identifier of the competition that the user is applying for")
                        ),
                        requestFields(userResourceFields),
                        responseFields(userResourceFields)
                ));
    }

    @Test
    public void getProfileAgreement() throws Exception {
        Long userId = 1L;
        ProfileAgreementResource profileAgreementResource = profileAgreementResourceBuilder.build();

        when(userProfileServiceMock.getProfileAgreement(userId)).thenReturn(serviceSuccess(profileAgreementResource));

        mockMvc.perform(get("/user/id/{id}/getProfileAgreement", userId))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile agreement being requested")
                        ),
                        responseFields(profileAgreementResourceFields)
                ));
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        Long userId = 1L;

        when(userProfileServiceMock.updateProfileAgreement(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileAgreement", userId))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile agreement for")
                        )
                ));
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkillsResource = profileSkillsResourceBuilder.build();

        when(userProfileServiceMock.getProfileSkills(userId)).thenReturn(serviceSuccess(profileSkillsResource));

        mockMvc.perform(get("/user/id/{id}/getProfileSkills", userId))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile skills being requested")
                        ),
                        responseFields(profileSkillsResourceFields)
                ));
    }

    @Test
    public void updateProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsEditResource profileSkillsEditResource = profileSkillsEditResourceBuilder.build();

        when(userProfileServiceMock.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileSkillsEditResource)))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile skills for")
                        ),
                        requestFields(profileSkillsEditResourceFields)
                ));
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> responses = affiliationResourceBuilder.build(2);
        when(userProfileServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/user/id/{id}/getUserAffiliations", userId))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
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
                .content(objectMapper.writeValueAsString(affiliations)))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with affiliations being updated")
                        ),
                        requestFields(fieldWithPath("[]").description("List of affiliations belonging to the user"))
                                .andWithPrefix("[].", affiliationResourceFields)
                ));
    }

    @Test
    public void getProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = userProfileResourceBuilder.build();

        when(userProfileServiceMock.getUserProfile(userId)).thenReturn(serviceSuccess(profileDetails));

        mockMvc.perform(get("/user/id/{id}/getUserProfile", userId))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile being requested")
                        ),
                        responseFields(userProfileResourceFields)
                ));
    }

    @Test
    public void updateProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = userProfileResourceBuilder.build();

        when(userProfileServiceMock.updateUserProfile(userId, profileDetails)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/id/{id}/updateUserProfile", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDetails)))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile for")
                        ),
                        requestFields(userProfileResourceFields)
                ));
    }
}
