package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.application.transactional.SectionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.*;

@Rollback
public class SectionControllerIntegrationTest extends BaseControllerIntegrationTest<SectionController> {

    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    SectionService sectionService;

    private Section section;
    private Long applicationId;
    private Section excludedSections;
    private Long sectionId;
    private Long excludedSectionId;
    private Long leadApplicantId;
    private Long collaboratorIdOne;
    private Long collaboratorOneProcessRoleId;
    private Long leadApplicantProcessRole;
    private long leadApplicantOrganisationId;
    private long collaboratorOneOrganisationId;
    private long sectionIdYourFinances;
    private long sectionIdLabour;



    @Before
    public void setUp() throws Exception {
        sectionId = 1L;
        excludedSectionId = 2L;
        applicationId = 1L;
        section = sectionRepository.findOne(sectionId);
        excludedSections = section = sectionRepository.findOne(excludedSectionId);

        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        leadApplicantOrganisationId = 3L;

        collaboratorIdOne = 8L;
        collaboratorOneProcessRoleId = 9L;
        collaboratorOneOrganisationId = 6L;

        sectionIdYourFinances = 7L;
        sectionIdLabour = 9L;
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(SectionController controller) {
        this.controller = controller;
    }

    @Test
    public void testGetById() throws Exception {
        Section section = controller.getById(sectionId);
        assertEquals("Details", section.getName());

        section = controller.getById(2L);
        assertEquals("Application questions", section.getName());
    }

    /**
     * Check if all sections under Your-Finances is marked-as-complete.
     */
    @Test
    public void testChildSectionsAreCompleteForAllOrganisations() throws Exception {
        excludedSections = null;

        section = sectionRepository.findOne(sectionIdYourFinances);
        assertEquals("Your finances", section.getName());
        assertTrue(section.hasChildSections());
        assertEquals(7, section.getChildSections().size());
        assertTrue(sectionService.childSectionsAreCompleteForAllOrganisations(section, applicationId, excludedSections));
        assertEquals(7, controller.getCompletedSections(applicationId, 3L).size());

        // Mark one question as incomplete.
        questionService.markAsInComplete(28L, applicationId, leadApplicantProcessRole);
        assertFalse(questionService.isMarkedAsComplete(questionService.getQuestionById(21L), applicationId, leadApplicantOrganisationId));

        assertFalse(sectionService.childSectionsAreCompleteForAllOrganisations(section, applicationId, excludedSections));
        assertEquals(6, controller.getCompletedSections(applicationId, leadApplicantOrganisationId).size());
        assertEquals(7, controller.getCompletedSections(applicationId, collaboratorOneOrganisationId).size());

        section = sectionRepository.findOne(11L);
        assertEquals("Materials", section.getName());
        assertFalse(section.hasChildSections());
    }
}