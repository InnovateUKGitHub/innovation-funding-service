package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.transactional.SectionService;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerTest extends BaseControllerMockMVCTest<SectionController> {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    protected SectionService sectionService;

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
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
        Competition competition = newCompetition().withSections(allSections).build();
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionService.getCompletedSections(application.getId(), organisationId))
                .thenReturn(completedSectionIds);

        mockMvc.perform(post("/section/getCompletedSections/" + application.getId() + "/" + organisationId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(completedSectionIds)))
                .andExpect(status().isOk());
    }

    @Test
    public void getIncompleteSectionsTest() throws Exception {
        // Initial data setup is not necessary, but gives an indication of how the data model should be.
        List<Section> completedSections = newSection().build(2);
        List<Section> incompleteSections = newSection().build(2);
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(completedSections);
        allSections.addAll(incompleteSections);
        List<Long> incompleteSectionIds = incompleteSections.stream().map(s -> s.getId()).collect(toList());
        Competition competition = newCompetition().withSections(allSections).build();
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionService.getIncompleteSections(application.getId()))
                .thenReturn(incompleteSectionIds);

        mockMvc.perform(post("/section/getIncompleteSections/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(incompleteSectionIds)))
                .andExpect(status().isOk());
    }

    @Test
    public void findByNameTest() throws Exception {
        List<Section> sections = newSection().build(1);
        when(sectionService.findByName("testname")).thenReturn(sections.get(0));

        mockMvc.perform(post("/section/findByName/testname")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(sections.get(0))));
    }

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section nextSection = newSection().build();
        when(sectionService.getNextSection(section.getId())).thenReturn(nextSection);

        mockMvc.perform(get("/section/getNextSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(nextSection)));
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section previousSection = newSection().build();
        when(sectionService.getPreviousSection(section.getId())).thenReturn(previousSection);

        mockMvc.perform(get("/section/getPreviousSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousSection)));
    }
}