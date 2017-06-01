package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Testing {@link ApplicantService}
 */
@Rollback
@Transactional
public class ApplicantServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    private static final long USER_ID = 1L;
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 1L;
    private static final long FINANCES_SECTION_ID = 22L;

    @Autowired
    private ApplicantService applicantService;

    @Test
    public void testGetQuestion() {
        loginSteveSmith();

        ServiceResult<ApplicantQuestionResource> result = applicantService.getQuestion(USER_ID, QUESTION_ID, APPLICATION_ID);

        assertThat(result.isSuccess(), equalTo(true));
        ApplicantQuestionResource applicantQuestion = result.getSuccessObjectOrThrowException();

        assertThat(applicantQuestion.getQuestion().getId(), equalTo(QUESTION_ID));
        assertThat(applicantQuestion.getCurrentUser().getId(), equalTo(USER_ID));
        assertThat(applicantQuestion.getApplication().getId(), equalTo(APPLICATION_ID));
        assertThat(applicantQuestion.getApplicantFormInputs().isEmpty(), equalTo(false));
        assertThat(applicantQuestion.getApplicants(), hasItem(applicantQuestion.getCurrentApplicant()));
        assertThat(applicantQuestion.getApplicantQuestionStatuses().isEmpty(), equalTo(false));
    }

    @Test
    public void testGetSection() {
        loginSteveSmith();

        ServiceResult<ApplicantSectionResource> result = applicantService.getSection(USER_ID, FINANCES_SECTION_ID, APPLICATION_ID);

        assertThat(result.isSuccess(), equalTo(true));
        ApplicantSectionResource applicantSection = result.getSuccessObjectOrThrowException();

        assertThat(applicantSection.getSection().getId(), equalTo(FINANCES_SECTION_ID));
        assertThat(applicantSection.getCurrentUser().getId(), equalTo(USER_ID));
        assertThat(applicantSection.getApplication().getId(), equalTo(APPLICATION_ID));
        assertThat(applicantSection.getApplicants(), hasItem(applicantSection.getCurrentApplicant()));
        assertThat(applicantSection.getApplicantParentSection(), notNullValue());
        assertThat(applicantSection.getApplicantChildrenSections().isEmpty(), equalTo(false));
    }

}
