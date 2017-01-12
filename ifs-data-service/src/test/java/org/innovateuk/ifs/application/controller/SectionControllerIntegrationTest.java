package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.application.transactional.SectionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuritySetter;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

@Rollback
public class SectionControllerIntegrationTest extends BaseControllerIntegrationTest<SectionController> {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private QuestionMapper questionMapper;

    private Section section;
    private Long applicationId;
    private Section excludedSections;
    private Long sectionId;
    private Long excludedSectionId;
    private Long collaboratorIdOne;
    private Long leadApplicantProcessRole;
    private Long leadApplicantOrganisationId;
    private Long collaboratorOneOrganisationId;
    private Long sectionIdYourProjectCostsFinances;
    private Long fundingSection;




    @Before
    public void setUp() throws Exception {
        sectionId = 1L;
        excludedSectionId = 2L;
        applicationId = 1L;
        section = sectionRepository.findOne(sectionId);
        excludedSections = section = sectionRepository.findOne(excludedSectionId);

        leadApplicantProcessRole = 1L;
        leadApplicantOrganisationId = 3L;

        collaboratorIdOne = 8L;
        collaboratorOneOrganisationId = 6L;

        sectionIdYourProjectCostsFinances = 16L;
        fundingSection = 18L;
        addBasicSecurityUser();
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(SectionController controller) {
        this.controller = controller;
    }

    @Test
    public void testGetById() throws Exception {
        SectionResource section = controller.getById(sectionId).getSuccessObject();
        assertEquals("Project details", section.getName());

        section = controller.getById(2L).getSuccessObject();
        assertEquals("Application questions", section.getName());
    }

    /**
     * Check if all sections under Your-Finances is marked-as-complete.
     */
    @Test
    public void testChildSectionsAreCompleteForAllOrganisations() throws Exception {
        excludedSections = null;

        section = sectionRepository.findOne(sectionIdYourProjectCostsFinances);
        assertEquals("Your project costs", section.getName());
        assertTrue(section.hasChildSections());
        assertEquals(7, section.getChildSections().size());
        assertTrue(sectionService.childSectionsAreCompleteForAllOrganisations(section, applicationId, excludedSections).getSuccessObject());
        assertEquals(8, controller.getCompletedSections(applicationId, 3L).getSuccessObject().size());

        // Mark one question as incomplete.
        questionService.markAsInComplete(new QuestionApplicationCompositeId(28L, applicationId), leadApplicantProcessRole);
	    Question question = questionService.getQuestionById(21L).andOnSuccessReturn(questionMapper::mapToDomain).getSuccessObject();
        assertFalse(questionService.isMarkedAsComplete(question, applicationId, leadApplicantOrganisationId).getSuccessObject());

        assertFalse(sectionService.childSectionsAreCompleteForAllOrganisations(section, applicationId, excludedSections).getSuccessObject());
        assertEquals(7, controller.getCompletedSections(applicationId, leadApplicantOrganisationId).getSuccessObject().size());

        UserResource collaborator = newUserResource().withId(collaboratorIdOne).build();
        SecuritySetter.swapOutForUser(collaborator);
        assertEquals(8, controller.getCompletedSections(applicationId, collaboratorOneOrganisationId).getSuccessObject().size());

        section = sectionRepository.findOne(11L);
        assertEquals("Materials", section.getName());
        assertFalse(section.hasChildSections());
    }

    @Test
    @Rollback
    public void testMarkAsComplete(){
        RestResult<List<ValidationMessages>> result = controller.markAsComplete(fundingSection, applicationId, leadApplicantProcessRole);
        assertTrue(result.isSuccess());
        List<ValidationMessages> validationMessages = result.getSuccessObject();
        Optional<ValidationMessages> findMessage = validationMessages.stream().filter(m -> m.getObjectId().equals(35L)).findFirst();
        assertTrue("Could not find ValidationMessage object", findMessage.isPresent());
        ValidationMessages messages = findMessage.get();
        assertEquals(1, messages.getErrors().size());
        assertEquals(new Long(35), messages.getObjectId());
        assertEquals("question", messages.getObjectName());

        assertThat(messages.getErrors(),
                contains(
                        allOf(
                                hasProperty("errorKey", is("validation.finance.min.row.other.funding.single"))
                        )
                )
        );
    }
}
