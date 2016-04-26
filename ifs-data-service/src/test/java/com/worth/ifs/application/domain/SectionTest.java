package com.worth.ifs.application.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.competition.domain.Competition;

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
        assertEquals(section.getQuestions(), questions);
        assertEquals(section.getId(), id);
        assertEquals(section.getName(), name);
        assertEquals(section.getParentSection(), parentSection);
    }
    
    @Test
    public void sectionWithNullTypeIsNotFinanceSection() {
    	assertFalse(section.isFinance());
    	assertFalse(section.isEachCollaboratorFinance());
    }
    
    @Test
    public void sectionWithAppropriateTypeIsFinanceSection() {
    	section.setType(SectionType.FINANCE);
    	assertTrue(section.isFinance());
    	assertFalse(section.isEachCollaboratorFinance());
    }
    
    @Test
    public void sectionWithAppropriateTypeIsEachCollaboratorFinanceSection() {
    	section.setType(SectionType.EACH_COLLABORATOR_FINANCE);
    	assertFalse(section.isFinance());
    	assertTrue(section.isEachCollaboratorFinance());
    }
}