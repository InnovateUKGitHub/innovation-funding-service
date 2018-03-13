package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerTest extends BaseControllerMockMVCTest<SectionController> {

    @Mock
    private BindingResult bindingResult;

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }


    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        SectionResource nextSection = newSectionResource().build();
        when(sectionServiceMock.getNextSection(section.getId())).thenReturn(serviceSuccess(nextSection));

        mockMvc.perform(get("/section/getNextSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(nextSection)));
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        SectionResource previousSection = newSectionResource().build();
        when(sectionServiceMock.getPreviousSection(section.getId())).thenReturn(serviceSuccess(previousSection));

        mockMvc.perform(get("/section/getPreviousSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(previousSection)));
    }

    @Test
    public void getByCompetitionIdVisibleForAssessment() throws Exception {
        List<SectionResource> expected = newSectionResource().build(2);

        long competitionId = 1L;

        when(sectionServiceMock.getByCompetitionIdVisibleForAssessment(competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/section/getByCompetitionIdVisibleForAssessment/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(sectionServiceMock, only()).getByCompetitionIdVisibleForAssessment(competitionId);
    }
}
