package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.SectionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.*;

@Rollback
public class SectionServiceIntegrationTest extends BaseControllerIntegrationTest<SectionService> {

    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    private Section section;
    private Long applicationId;
    private Section excludedSections;
    private Long sectionId;
    private Long excludedSectionId;
    private QuestionService questionService;
    private Long leadApplicantId;
    private Long collaboratorIdOne;
    private Long collaboratorOneProcessRoleId;
    private Long leadApplicantProcessRole;
    private long leadApplicantOrganisationId;
    private long collaboratorOneOrganisationId;
    private long sectionIdYourFinances;
    private long sectionIdLabour;

    @Autowired
    public void setQuestionService(QuestionService questionService) {
        this.questionService = questionService;
    }

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
    protected void setControllerUnderTest(SectionService service) {
        this.controller = service;
    }


    @Test
    public void testMarkSectionAsInComplete() throws Exception {
        // Mark one question as incomplete.
        questionService.markAsInComplete(28L, applicationId, leadApplicantProcessRole);
        assertFalse(questionService.isMarkedAsComplete(questionService.getQuestionById(21L), applicationId, leadApplicantOrganisationId));
    }
}