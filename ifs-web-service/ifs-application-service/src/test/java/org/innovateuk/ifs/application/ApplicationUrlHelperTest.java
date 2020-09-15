package org.innovateuk.ifs.application;

import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ApplicationUrlHelperTest {
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 2L;

    @Test
    public void questionUrlForNoQuestionType() {
        // given
        QuestionSetupType questionSetupType = null;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void questionUrlForApplicationDetails() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.APPLICATION_DETAILS;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/application-details"), result);
    }

    @Test
    public void questionUrlForGrantAgreement() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.GRANT_AGREEMENT;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/grant-agreement"), result);
    }

    @Test
    public void questionUrlForGrantTransferDetails() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.GRANT_TRANSFER_DETAILS;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/grant-transfer-details"), result);
    }

    @Test
    public void questionUrlForApplicationTeam() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.APPLICATION_TEAM;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/team"), result);
    }

    @Test
    public void questionUrlForTermsAndConditions() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.TERMS_AND_CONDITIONS;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/terms-and-conditions"), result);
    }

    @Test
    public void questionUrlForResearchCategory() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.RESEARCH_CATEGORY;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/research-category"), result);
    }

    @Test
    public void questionUrlForEqualityDiversityInclusion() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION;

        // when
        Optional<String> result = ApplicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/generic"), result);
    }
}
