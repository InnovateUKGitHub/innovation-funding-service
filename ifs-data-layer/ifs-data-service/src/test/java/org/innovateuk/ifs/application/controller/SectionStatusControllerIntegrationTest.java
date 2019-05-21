package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.junit.Assert.*;

@Rollback
public class SectionStatusControllerIntegrationTest extends BaseControllerIntegrationTest<SectionStatusController> {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionStatusService questionStatusService;
    @Autowired
    private QuestionMapper questionMapper;

    private Section section;
    private Long applicationId;
    private Long sectionId;
    private Long collaboratorIdOne;
    private Long leadApplicantProcessRole;
    private Long leadApplicantOrganisationId;
    private Long collaboratorOneOrganisationId;
    private Long sectionIdYourProjectCostsFinances;
    private Long fundingSection;

    @Before
    public void setUp() throws Exception {
        sectionId = 1L;
        applicationId = 1L;
        section = sectionRepository.findById(sectionId).get();

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
    protected void setControllerUnderTest(SectionStatusController controller) {
        this.controller = controller;
    }

    @Test
    @Rollback
    public void testMarkAsComplete(){
        RestResult<List<ValidationMessages>> result = controller.markAsComplete(fundingSection, applicationId, leadApplicantProcessRole);
        assertTrue(result.isSuccess());
        List<ValidationMessages> validationMessages = result.getSuccess();
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
