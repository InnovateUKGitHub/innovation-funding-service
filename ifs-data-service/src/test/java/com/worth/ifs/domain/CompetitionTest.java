package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompetitionTest {
    Competition competition;

    Long id;
    List<Application> applications;
    List<Question> questions;
    List<Section> sections;
    String name;
    String description;
    LocalDate startDate;
    LocalDate endDate;

    @Before
    public void setUp() throws Exception {
        id = 0L;

        name = "testCompetitionName";
        description = "testCompetitionDescription";
        startDate = LocalDate.now();
        endDate = LocalDate.now().plusDays(5);

        sections = new ArrayList<Section>();
        sections.add(new Section());
        sections.add(new Section());
        sections.add(new Section());

        competition = new Competition(id, applications, questions, sections, name, description, startDate, endDate);
    }

    @Test
    public void competitionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(competition.getId(), id);
        Assert.assertEquals(competition.getName(), name);
        Assert.assertEquals(competition.getDescription(), description);
        Assert.assertEquals(competition.getStartDate(), startDate);
        Assert.assertEquals(competition.getEndDate(), endDate);
        Assert.assertEquals(competition.getSections(), sections);
    }
}