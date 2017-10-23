package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.EditUserResourceDocs;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.documentation.UserDocs.internalUserRegistrationResourceFields;
import static org.innovateuk.ifs.documentation.UserDocs.userPageResourceFields;
import static org.innovateuk.ifs.documentation.UserDocs.userResourceFields;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.UserRoleType.externalApplicantRoles;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
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
        when(baseUserServiceMock.findByProcessRole(eq(INNOVATION_LEAD))).thenReturn(serviceSuccess(asList(userResource, userResource)));

        mockMvc.perform(get("/user/findByRole/{userRole}", INNOVATION_LEAD))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userRoleName").description("The name of the role to get the users by.")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of users with the selected role, ordered by first name, lastn ame")
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
    public void testFindActiveInternalUsers() throws Exception {
        UserPageResource userPageResource = buildUserPageResource();
        when(userServiceMock.findActiveByProcessRoles(UserRoleType.internalRoles(), new PageRequest(0, 5))).thenReturn(serviceSuccess(userPageResource));
        mockMvc.perform(get(buildPaginationUri("/user/internal/active", 0, 5, null, new LinkedMultiValueMap<>()))).andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        responseFields(userPageResourceFields)
                ));
    }

    @Test
    public void testFindInactiveInternalUsers() throws Exception {
        UserPageResource userPageResource = buildUserPageResource();
        when(userServiceMock.findInactiveByProcessRoles(UserRoleType.internalRoles(), new PageRequest(0, 5))).thenReturn(serviceSuccess(userPageResource));
        mockMvc.perform(get(buildPaginationUri("/user/internal/inactive", 0, 5, null, new LinkedMultiValueMap<>()))).andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        responseFields(userPageResourceFields)
                ));
    }

    private UserPageResource buildUserPageResource(){
        UserPageResource pageResource = new UserPageResource();
        pageResource.setNumber(5);
        pageResource.setSize(5);
        pageResource.setTotalElements(10);
        pageResource.setTotalPages(2);
        pageResource.setContent(newUserResource().withEmail("example@innovateuk.test").build(5));
        return pageResource;
    }

    @Test
    public void createInternalUser() throws Exception {

        List<RoleResource> roleResources = newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build(1);
        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withRoles(roleResources)
                .build();

        when(registrationServiceMock.createInternalUser("SomeHashString", internalUserRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/internal/create/{inviteHash}", "SomeHashString")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(internalUserRegistrationResource)))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("inviteHash").description("Hash from invite to be used for creating new account")
                        ),
                        requestFields(internalUserRegistrationResourceFields)
                ));
    }

    @Test
    public void editInternalUser() throws Exception {

        EditUserResource editUserResource = new EditUserResource(1L, "Johnathan", "Dow", UserRoleType.SUPPORT);
        when(registrationServiceMock.editInternalUser(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/internal/edit")
                .contentType(APPLICATION_JSON)
                .content(toJson(editUserResource)))
                .andExpect(status().isOk())
                .andDo(document("user/internal/edit/{method-name}",
                        requestFields(EditUserResourceDocs.editUserResourceFields)
                ));

        verify(registrationServiceMock).editInternalUser(any(), any());
    }

    @Test
    public void deactivateUser() throws Exception {
        final Long userId = 9999L;

        when(registrationServiceMock.deactivateUser(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/user/id/{userId}/deactivate", userId))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user being deactivated")
                        )
                ));
    }

    @Test
    public void reactivateUser() throws Exception {
        final Long userId = 9999L;

        when(registrationServiceMock.activateUser(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(get("/user/id/{userId}/reactivate", userId))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user being reactivated")
                        )
                ));
    }

    @Test
    public void findAllExternal() throws Exception {
        when(userServiceMock.findAllByProcessRoles(externalApplicantRoles())).thenReturn(serviceSuccess(newUserOrganisationResource().build(2)));

        mockMvc.perform(get("/user/findAllExternal"))
                .andDo(document("user/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of external users with associated organisations")
                        )
                ));
    }
}
