package org.innovateuk.ifs.profile.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.profile.controller.ProfileController;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceBuilder;
import static org.innovateuk.ifs.documentation.ProfileAgreementDocs.profileAgreementResourceFields;
import static org.innovateuk.ifs.documentation.ProfileSkillsDocs.*;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceBuilder;
import static org.innovateuk.ifs.documentation.UserProfileResourceDocs.userProfileResourceFields;
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

    @Override
    protected ProfileController supplyControllerUnderTest() {
        return new ProfileController();
    }

    @Test
    public void getProfileAgreement() throws Exception {
        Long userId = 1L;
        ProfileAgreementResource profileAgreementResource = profileAgreementResourceBuilder.build();

        when(profileServiceMock.getProfileAgreement(userId)).thenReturn(serviceSuccess(profileAgreementResource));

        mockMvc.perform(get("/profile/id/{id}/getProfileAgreement", userId))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with the profile agreement being requested")
                        ),
                        responseFields(profileAgreementResourceFields)
                ));
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        Long userId = 1L;

        when(profileServiceMock.updateProfileAgreement(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateProfileAgreement", userId))
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

        mockMvc.perform(get("/profile/id/{id}/getProfileSkills", userId))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
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

        when(profileServiceMock.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileSkillsEditResource)))
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

        mockMvc.perform(get("/profile/id/{id}/getUserProfile", userId))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
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

        when(profileServiceMock.updateUserProfile(userId, profileDetails)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateUserProfile", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDetails)))
                .andExpect(status().isOk())
                .andDo(document("profile/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user to update the profile for")
                        ),
                        requestFields(userProfileResourceFields)
                ));
    }
}
