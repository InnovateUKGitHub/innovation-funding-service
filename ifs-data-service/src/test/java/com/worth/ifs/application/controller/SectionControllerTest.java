package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerTest extends BaseControllerMockMVCTest<SectionController> {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
    }

    @Test
    public void getCompletedSectionsTest() throws Exception {
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        mockMvc.perform(post("/section/getCompletedSections/123/456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void getIncompleteSectionsTest() throws Exception {
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);

        mockMvc.perform(post("/section/getIncompleteSections/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void findByNameTest() throws Exception {
        List<Section> sections = newSection().build(1);
        when(sectionRepositoryMock.findByName("testname")).thenReturn(sections.get(0));

        mockMvc.perform(post("/section/findByName/testname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(sections.get(0))));
    }

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section nextSection = newSection().build();
        when(sectionRepositoryMock.findOne(1L)).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(
                section.getCompetition().getId(), section.getPriority()
        )).thenReturn(nextSection);

        Section returnSection = controller.getNextSection(1L);
        Assert.assertEquals(nextSection, returnSection);
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section previousSection = newSection().build();
        when(sectionRepositoryMock.findOne(1L)).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(
                section.getCompetition().getId(), section.getPriority()
        )).thenReturn(previousSection);

        Section returnSection = controller.getPreviousSection(1L);
        Assert.assertEquals(previousSection, returnSection);
    }

    @Test
    public void getNextSectionWitParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        when(sectionRepositoryMock.findOne(2L)).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority()
        )).thenReturn(siblingSection);

        Section returnSection = controller.getNextSection(2L);
        Assert.assertEquals(siblingSection, returnSection);
    }

    @Test
    public void getPreviousSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        when(sectionRepositoryMock.findOne(2L)).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority()
        )).thenReturn(siblingSection);

        Section returnSection = controller.getPreviousSection(2L);
        Assert.assertEquals(siblingSection, returnSection);
    }
}