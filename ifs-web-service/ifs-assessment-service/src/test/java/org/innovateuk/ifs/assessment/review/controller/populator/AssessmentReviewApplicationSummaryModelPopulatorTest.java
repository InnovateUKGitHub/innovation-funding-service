package org.innovateuk.ifs.assessment.review.controller.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentReviewApplicationSummaryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator populator;

    @Mock
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private SectionService sectionService;

    @Test
    public void populateModel() {

       //TODO
    }
}
