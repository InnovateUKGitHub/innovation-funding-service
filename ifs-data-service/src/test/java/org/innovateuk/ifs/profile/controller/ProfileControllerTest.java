package org.innovateuk.ifs.profile.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileControllerTest extends BaseControllerMockMVCTest<ProfileController> {

    @Override
    protected ProfileController supplyControllerUnderTest() {
        return new ProfileController();
    }

    @Test
    public void getProfileSkills() throws Exception {
        Long userId = 1L;
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource().build();

        when(profileServiceMock.getProfileSkills(userId)).thenReturn(serviceSuccess(profileSkillsResource));

        mockMvc.perform(get("/profile/id/{id}/getProfileSkills", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileSkillsResource)));

        verify(profileServiceMock, only()).getProfileSkills(userId);
    }

    @Test
    public void updateProfileSkills() throws Exception {
        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withSkillsAreas(RandomStringUtils.random(5000))
                .build();

        Long userId = 1L;

        when(profileServiceMock.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileSkillsEditResource)))
                .andExpect(status().isOk());

        verify(profileServiceMock, only()).updateProfileSkills(userId, profileSkillsEditResource);
    }

    @Test
    public void updateProfileSkills_invalid() throws Exception {
        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withSkillsAreas(RandomStringUtils.random(5001))
                .build();

        Long userId = 1L;

        when(profileServiceMock.updateProfileSkills(userId, profileSkillsEditResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateProfileSkills", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileSkillsEditResource)))
                .andExpect(status().isNotAcceptable());

        verify(profileServiceMock, never()).updateProfileSkills(userId, profileSkillsEditResource);
    }

    @Test
    public void getProfileAgreement() throws Exception {
        Long userId = 1L;
        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource().build();

        when(profileServiceMock.getProfileAgreement(userId)).thenReturn(serviceSuccess(profileAgreementResource));

        mockMvc.perform(get("/profile/id/{id}/getProfileAgreement", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileAgreementResource)));

        verify(profileServiceMock, only()).getProfileAgreement(userId);
    }

    @Test
    public void updateProfileAgreement() throws Exception {
        Long userId = 1L;

        when(profileServiceMock.updateProfileAgreement(userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{id}/updateProfileAgreement", userId))
                .andExpect(status().isOk());

        verify(profileServiceMock, only()).updateProfileAgreement(userId);
    }

    @Test
    public void getProfileAddress() throws Exception {
        Long userId = 1L;
        UserProfileResource profileDetails = newUserProfileResource().build();

        when(profileServiceMock.getUserProfile(userId)).thenReturn(serviceSuccess(profileDetails));

        mockMvc.perform(get("/profile/id/{userId}/getUserProfile", userId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(profileDetails)));

        verify(profileServiceMock, only()).getUserProfile(userId);
    }

    @Test
    public void updateProfileAddress() throws Exception {
        UserProfileResource profileDetails = newUserProfileResource().build();
        Long userId = 1L;

        when(profileServiceMock.updateUserProfile(userId, profileDetails)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/profile/id/{userId}/updateUserProfile", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDetails)))
                .andExpect(status().isOk());

        verify(profileServiceMock, only()).updateUserProfile(userId, profileDetails);
    }

    @Test
    public void getUserProfileStatus() throws Exception {
        UserProfileStatusResource profileStatus = newUserProfileStatusResource().build();
        Long userId = 1L;

        when(profileServiceMock.getUserProfileStatus(userId)).thenReturn(serviceSuccess(profileStatus));

        mockMvc.perform(get("/profile/id/{userId}/profileStatus", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileStatus)))
                .andExpect(status().isOk());

        verify(profileServiceMock, only()).getUserProfileStatus(userId);
    }

}
