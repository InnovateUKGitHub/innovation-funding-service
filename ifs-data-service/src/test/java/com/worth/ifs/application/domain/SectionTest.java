package com.worth.ifs.application.domain;

import com.worth.ifs.competition.domain.Competition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SectionTest {
    Section section;

    Long id;
    Competition competition;
    List<Question> questions;
    String name;
    Section parentSection;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = new Competition();
        questions = new ArrayList<>();
        questions.add(new Question());
        questions.add(new Question());
        questions.add(new Question());
        parentSection = new Section();
        name = "testSectionName";

        section = new Section(id, competition, questions, name, parentSection);
    }

    @Test
    public void sectionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(section.getQuestions(), questions);
        Assert.assertEquals(section.getId(), id);
        Assert.assertEquals(section.getName(), name);
        Assert.assertEquals(section.getParentSection(), parentSection);
    }
}