package com.worth.ifs.domain;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SectionTest {
    Section section;

    Long id;
    Competition competition;
    List<Question> questions;
    String name;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        competition = new Competition();
        questions = new ArrayList<Question>();
        questions.add(new Question());
        questions.add(new Question());
        questions.add(new Question());

        name = "testSectionName";

        section = new Section(id, competition, questions, name);
    }

    @Test
    public void sectionShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(section.getQuestions(), questions);
        Assert.assertEquals(section.getId(), id);
        Assert.assertEquals(section.getName(), name);
    }
}