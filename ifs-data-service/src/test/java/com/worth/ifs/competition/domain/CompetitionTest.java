package com.worth.ifs.competition.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompetitionTest {
    Competition competition;

    Long id;
    List<Application> applications;
    List<Question> questions;
    List<Section> sections;
    String name;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer maxResearchRatio;
    Integer academicGrantPercentage;
    LocalDateTime assemmentStartDate;
    LocalDateTime assemmentEndDate;

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
//        Assert.assertEquals(competition.getStartDate(), startDate);
//        Assert.assertEquals(competition.getEndDate(), endDate);
        assertEquals(competition.getSections(), sections);
        assertEquals(competition.getMaxResearchRatio(), maxResearchRatio);
        assertEquals(competition.getAcademicGrantPercentage(), academicGrantPercentage);
    }

    @Test
    public void competitionStatusOpen(){
        assertEquals(CompetitionResource.Status.OPEN, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusNotStarted(){
        competition.setStartDate(LocalDateTime.now().plusDays(1));
        assertEquals(CompetitionResource.Status.NOT_STARTED, competition.getCompetitionStatus());
    }

    @Test
    public void competitionStatusInAssessment(){
        competition.setEndDate(LocalDateTime.now().minusDays(1));
        competition.setAssessmentStartDate(LocalDateTime.now().minusDays(1));
        assertEquals(CompetitionResource.Status.IN_ASSESSMENT, competition.getCompetitionStatus());
    }
}