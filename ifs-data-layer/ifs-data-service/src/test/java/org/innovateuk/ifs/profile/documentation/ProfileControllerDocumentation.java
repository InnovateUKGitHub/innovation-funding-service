package org.innovateuk.ifs.profile.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.AddressDocs;
import org.innovateuk.ifs.documentation.AgreementDocs;
import org.innovateuk.ifs.documentation.InnovationAreaResourceDocs;
import org.innovateuk.ifs.profile.controller.ProfileController;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceBuilder;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceFields;
import static org.innovateuk.ifs.documentation.ProfileSkillsDocs.*;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceBuilder;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceFields;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerDocumentation extends BaseControllerMockMVCTest<ProfileController> {

    @Mock
    private ProfileService profileServiceMock;

    @Override
    protected ProfileController supplyControllerUnderTest() {
        return new ProfileController();
    }

    @Test
    public void getProfileAgreement() throws Exception {
        Long userId = 1L;
        ProfileAgreementResource profileAgreementResource = profileAgreementResourceBuilder.build();

        when(profileServiceMock.getProfileAgreement(userId)).thenReturn(serviceSuccess(profileAgreementResource));

        mockMvc.perform(get("/profile/id/{id}/get-profile-agreement", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile agreement being requested")
                        ),
                        responseFields(profileAgreementResourceFields)
                        .andWithPrefix("agreement.", AgreementDocs.agreementResourceFields)
                ));
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        Long userId = 1L;

        when(profileServiceMock.updateProfileAgreement(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/update-profile-agreement", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile agreement for")
                        )
                ));
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkillsResource = profileSkillsResourceBuilder.build();

        when(profileServiceMock.getProfileSkills(userId)).thenReturn(serviceSuccess(profileSkillsResource));

        mockMvc.perform(get("/profile/id/{id}/get-profile-skills", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile skills being requested")
                        ),
                        responseFields(profileSkillsResourceFields)
                        .andWithPrefix("innovationAreas[].", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void updateProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsEditResource profileSkillsEditResource = profileSkillsEditResourceBuilder.build();

        when(profileServiceMock.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/update-profile-skills", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileSkillsEditResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile skills for")
                        ),
                        requestFields(profileSkillsEditResourceFields)
                ));
    }

    @Test
    public void getProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = userProfileResourceBuilder.build();

        when(profileServiceMock.getUserProfile(userId)).thenReturn(serviceSuccess(profileDetails));

        mockMvc.perform(get("/profile/id/{id}/get-user-profile", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile being requested")
                        ),
                        responseFields(userProfileResourceFields)
                                .andWithPrefix("address.", AddressDocs.addressResourceFields)
                ));
    }

    @Test
    public void updateProfileDetails() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = userProfileResourceBuilder.build();

        when(profileServiceMock.updateUserProfile(userId, profileDetails)).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(put("/profile/id/{id}/update-user-profile", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDetails))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile for")
                        ),
                        requestFields(userProfileResourceFields)
                                .andWithPrefix("address.", AddressDocs.addressResourceFields)
                ));
    }
}
