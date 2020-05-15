package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.EditUserResourceDocs;
import org.innovateuk.ifs.documentation.UserDocs;
import org.innovateuk.ifs.documentation.UserOrganisationResourceDocs;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.UserDocs.*;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.ManageUserResourceBuilder.newManageUserResource;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.externalApplicantRoles;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocumentation extends BaseControllerMockMVCTest<UserController> {

    @Mock
    private UserService userService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private BaseUserService baseUserService;

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Test
    public void sendEmailVerificationNotification() throws Exception {
        final String emailAddress = "sample@me.com";

        final UserResource userResource = newUserResource().build();

        when(userService.findInactiveByEmail(emailAddress)).thenReturn(serviceSuccess(userResource));

        mockMvc.perform(put("/user/send-email-verification-notification/{emailAddress}/", emailAddress)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("emailAddress").description("E-mail address of the user who a verification link should be sent to by e-mail")
                        )
                ));
    }

    @Test
    public void createUser() throws Exception {
        final long organisationId = 9999L;

        final UserResource userResource = newUserResource().build();
        when(registrationService.createUser(userResource)).thenReturn(serviceSuccess(userResource));

        mockMvc.perform(post("/user/create-lead-applicant-for-organisation/{organisationId}", organisationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
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
        when(baseUserService.findByProcessRole(eq(INNOVATION_LEAD))).thenReturn(serviceSuccess(asList(userResource, userResource)));

        mockMvc.perform(get("/user/find-by-role/{userRole}", INNOVATION_LEAD)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userRole").description("The role to get the users by.")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of users with the selected role, ordered by first name, last name")
                        ).andWithPrefix("[].", userResourceFields)
                ));
    }

    @Test
    public void findByProcessRoleAndUserStatus() throws Exception {

        final UserResource userResource = newUserResource().build();
        when(baseUserService.findByProcessRoleAndUserStatus(eq(INNOVATION_LEAD), eq(UserStatus.ACTIVE))).thenReturn(serviceSuccess(asList(userResource, userResource)));

        mockMvc.perform(get("/user/find-by-role-and-status/{userRole}/{userStatus}", INNOVATION_LEAD, UserStatus.ACTIVE)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userRole").description("The role to get the users by."),
                                parameterWithName("userStatus").description("The status to get the users by.")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of users with the selected role, ordered by first name, last name")
                        ).andWithPrefix("[].", userResourceFields)
                ));
    }

    @Test
    public void createUserWithCompetitionId() throws Exception {
        final long organisationId = 9999L;
        final long competitionId = 8888L;

        final UserResource userResource = newUserResource().build();
        when(registrationService.createUserWithCompetitionContext(competitionId, organisationId, userResource)).thenReturn(serviceSuccess(userResource));

        mockMvc.perform(post("/user/create-lead-applicant-for-organisation/{organisationId}/{competitionId}", organisationId, competitionId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
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
    public void findActive() throws Exception {
        ManageUserPageResource userPageResource = buildManageUserPageResource();
        when(userService.findActive("filter", PageRequest.of(0, 5, UserController.DEFAULT_USER_SORT))).thenReturn(serviceSuccess(userPageResource));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", "filter");
        mockMvc.perform(get(buildPaginationUri("/user/active", 0, 5, null, params))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        responseFields(userPageResourceFields)
                        .andWithPrefix("content[].", UserDocs.manageUserResourceFields)
                ));
    }

    @Test
    public void findInactive() throws Exception {
        ManageUserPageResource userPageResource = buildManageUserPageResource();
        when(userService.findInactive("filter", PageRequest.of(0, 5, UserController.DEFAULT_USER_SORT))).thenReturn(serviceSuccess(userPageResource));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", "filter");
        mockMvc.perform(get(buildPaginationUri("/user/inactive", 0, 5, null, params))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        responseFields(userPageResourceFields)
                        .andWithPrefix("content[].", UserDocs.manageUserResourceFields)
                ));
    }

    private ManageUserPageResource buildManageUserPageResource(){
        ManageUserPageResource pageResource = new ManageUserPageResource();
        pageResource.setNumber(5);
        pageResource.setSize(5);
        pageResource.setTotalElements(10);
        pageResource.setTotalPages(2);
        pageResource.setContent(newManageUserResource().withEmail("example@innovateuk.test").build(5));
        return pageResource;
    }

    @Test
    public void createInternalUser() throws Exception {

        List<Role> roleResources = singletonList(Role.PROJECT_FINANCE);
        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withRoles(roleResources)
                .build();

        when(registrationService.createInternalUser("SomeHashString", internalUserRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/user/internal/create/{inviteHash}", "SomeHashString")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(internalUserRegistrationResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("inviteHash").description("Hash from invite to be used for creating new account")
                        ),
                        requestFields(internalUserRegistrationResourceFields)
                ));
    }

    @Test
    public void agreeNewSiteTermsAndConditions() throws Exception {
        long userId = 1L;

        when(userService.agreeNewTermsAndConditions(1L)).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/id/{userId}/agree-new-site-terms-and-conditions", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user agreeing to the site " +
                                        "terms and conditions")
                        )
                ));
    }

    @Test
    public void editInternalUser() throws Exception {

        EditUserResource editUserResource = new EditUserResource(1L, "Johnathan", "Dow", Role.SUPPORT);
        when(registrationService.editInternalUser(any(), any())).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/internal/edit")
                .contentType(APPLICATION_JSON)
                .content(toJson(editUserResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("user/internal/edit/{method-name}",
                        requestFields(EditUserResourceDocs.editUserResourceFields)
                ));

        verify(registrationService).editInternalUser(any(), any());
    }

    @Test
    public void deactivateUser() throws Exception {
        final long userId = 9999L;

        when(registrationService.deactivateUser(userId)).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(get("/user/id/{userId}/deactivate", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user being deactivated")
                        )
                ));
    }

    @Test
    public void reactivateUser() throws Exception {
        final long userId = 9999L;

        when(registrationService.activateUser(userId)).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(get("/user/id/{userId}/reactivate", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user being reactivated")
                        )
                ));
    }

    @Test
    public void findExternalUsers() throws Exception {

        String searchString = "aar";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<UserOrganisationResource> userOrganisationResources = newUserOrganisationResource().build(2);
        when(userService.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory)).thenReturn(serviceSuccess(userOrganisationResources));

        mockMvc.perform(get("/user/find-external-users?searchString=" + searchString + "&searchCategory=" + searchCategory)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(userOrganisationResources)))
                .andDo(document(
                        "user/{method-name}",
                        requestParameters(
                                parameterWithName("searchString").description("The string to search"),
                                parameterWithName("searchCategory").description("The category to search")
                        )
                        ,
                        responseFields(
                                fieldWithPath("[]").description("List of external users with associated organisations, which contain the search string and match the search category")
                        )                        .andWithPrefix("[].", UserOrganisationResourceDocs.userOrganisationResourceFields)

                ));

        verify(userService).findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);
    }

    @Test
    public void grantRole() throws Exception {
        long userId = 1L;
        Role grantRole = Role.APPLICANT;

        when(userService.grantRole(new GrantRoleCommand(userId, grantRole))).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(post("/user/{userId}/grant/{role}", userId, grantRole.name())
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "user/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("The user to grant the role for"),
                                parameterWithName("role").description("The role to grant")
                        )
                ));

        verify(userService).grantRole(new GrantRoleCommand(userId, grantRole));
    }
}