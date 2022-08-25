package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KtpAssessmentFeedbackViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpAssessmentFeedbackViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtp() {
        final String questionShortName = "Technical approach";

        AssessmentResource assessmentResource = newAssessmentResource()
                .withApplication(1L)
                .build();
        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(fundingType)
                .withAssessorDeadlineDate(ZonedDateTime.now())
                .withAssessorAcceptsDate(ZonedDateTime.now())
                .build();
        QuestionResource questionResource = newQuestionResource()
                .withShortName(questionShortName)
                .build();
        List<FormInputResource> formInputResources = newFormInputResource().build(2);

        AssessmentFeedbackViewModel viewModel = new AssessmentFeedbackViewModel(assessmentResource, competitionResource,
                questionResource, false, null, null, formInputResources,
                false, false, null, null,
                "template document", null);

        assertTrue(viewModel.isKtp());
    }
}
