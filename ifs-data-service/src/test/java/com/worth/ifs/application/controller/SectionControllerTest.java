package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.SectionBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerTest extends BaseControllerMockMVCTest<SectionController> {

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }

    @Test
    public void testGetCompletedSections() throws Exception {
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        mockMvc.perform(post("/section/getCompletedSections/123/456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testGetIncompleteSections() throws Exception {
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        mockMvc.perform(post("/section/getIncompleteSections/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void testFindByName() throws Exception {
        List<Section> sections = newSection().build(1);
        when(sectionRepositoryMock.findByName("testname")).thenReturn(sections.get(0));

        mockMvc.perform(post("/section/findByName/testname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(sections.get(0))));
    }
}