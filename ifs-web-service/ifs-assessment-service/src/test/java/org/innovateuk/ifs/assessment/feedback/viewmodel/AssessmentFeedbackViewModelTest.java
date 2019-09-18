package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AssessmentFeedbackViewModelTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        final String questionShortName = "Technical approach";

        AssessmentResource assessmentResource = newAssessmentResource().withApplication(1L).build();
        CompetitionResource competitionResource = newCompetitionResource().withAssessorDeadlineDate(ZonedDateTime.now()).withAssessorAcceptsDate(ZonedDateTime.now()).build();
        QuestionResource questionResource = newQuestionResource().withShortName(questionShortName).build();
        List<FormInputResource> formInputResources = newFormInputResource().build(2);

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(
                assessmentResource,
                competitionResource,
                questionResource,
                null,
                formInputResources,
                false,
                false,
                null,
                null,
                "template document",
                null
        );

        assertEquals("View technical approach appendix", assessmentFeedbackViewModel.getAppendixFileDescription());
        assertEquals("View template document", assessmentFeedbackViewModel.getTemplateDocumentFileDescription());
        assertFalse(assessmentFeedbackViewModel.isAppendixExists());
        assertFalse(assessmentFeedbackViewModel.isTemplateDocumentExists());
    }
}
