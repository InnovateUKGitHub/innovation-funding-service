package com.worth.ifs.competition.domain;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;

public class CompetitionTest {
    private Competition competition;

    private Long id;
    private List<Application> applications;
    private List<Question> questions;
    private List<Section> sections;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;
    private LocalDateTime assemmentStartDate;
    private LocalDateTime assemmentEndDate;

    @Before
    public void setUp() throws Exception {
        id = 0L;

        name = "testCompetitionName";
        description = "testCompetitionDescription";
        startDate = LocalDateTime.now().minusDays(5);
        endDate = startDate.plusDays(15);
        assemmentStartDate = endDate;
        assemmentEndDate = endDate.plusDays(15);

        maxResearchRatio = 10;
        academicGrantPercentage = 30;

        sections = new ArrayList<>();
        sections.add(new Section());
        sections.add(new Section());
        sections.add(new Section());

        competition = new Competition(id, applications, questions, sections, name, description, startDate, endDate);
        competition.setAssessmentStartDate(assemmentStartDate);
        competition.setAssessmentEndDate(assemmentEndDate);
        competition.setMaxResearchRatio(maxResearchRatio);
        competition.setAcademicGrantPercentage(academicGrantPercentage);
    }

    @Test
    public void competitionShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(competition.getId(), id);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getDescription(), description);
        assertEquals(competition.getSections(), sections);
        assertEquals(competition.getMaxResearchRatio(), maxResearchRatio);
        assertEquals(competition.getAcademicGrantPercentage(), academicGrantPercentage);
    }

}