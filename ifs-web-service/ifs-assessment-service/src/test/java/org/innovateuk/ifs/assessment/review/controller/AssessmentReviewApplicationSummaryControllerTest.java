package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;


@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentReviewApplicationSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentReviewApplicationSummaryController> {

    @Spy
    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private InviteService inviteService;

    @Override
    protected AssessmentReviewApplicationSummaryController supplyControllerUnderTest() {
        return new AssessmentReviewApplicationSummaryController();
    }

    //TODO
}
