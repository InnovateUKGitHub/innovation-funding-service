package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.domain.Section;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.error.ValidationMessages.reject;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionStatusControllerTest extends BaseControllerMockMVCTest<SectionStatusController> {

    @Mock
    private SectionStatusService sectionStatusServiceMock;

    @Mock
    private BindingResult bindingResult;

    @Override
    protected SectionStatusController supplyControllerUnderTest() {
        return new SectionStatusController();
    }

    @Test
    public void getCompletedSectionsTest() throws Exception {
        // Initial data setup is not necessary, but gives an indication of how the data model should be.
        long organisationId = 1L;
        List<Section> completedSections = newSection().build(2);
        List<Section> incompleteSections = newSection().build(2);
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(completedSections);
        allSections.addAll(incompleteSections);
        Set<Long> completedSectionIds = completedSections.stream().map(s -> s.getId()).collect(toSet());
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionStatusServiceMock.getCompletedSections(application.getId(), organisationId))
                .thenReturn(serviceSuccess(completedSectionIds));

        mockMvc.perform(get("/section-status/get-completed-sections/" + application.getId() + "/" + organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(completedSectionIds)));
    }

    @Test
    public void markSectionAsComplete() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(noErrors()));

        mockMvc.perform(MockMvcRequestBuilders.post("/section-status/mark-as-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(noErrors())));
    }

    @Test
    public void markSectionAsInComplete() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsInComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/section-status/mark-as-in-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk());
    }

    @Test
    public void markSectionAsNotRequired() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsNotRequired(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/section-status/mark-as-not-required/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk());
    }

    @Test
    public void markSectionAsCompleteInvalid() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        bindingResult = new BeanPropertyBindingResult(section, "costItem");
        reject(bindingResult, "validation.finance.min.row");
        ValidationMessages messages = new ValidationMessages(1L, bindingResult);
        when(sectionStatusServiceMock.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(messages));


        mockMvc.perform(MockMvcRequestBuilders.post("/section-status/mark-as-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(messages)));
    }

}
