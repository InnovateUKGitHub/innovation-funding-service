package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationUrlHelperTest {
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 2L;
    private static final long ORGANISATION_ID = 3L;

    @InjectMocks
    private ApplicationUrlHelper applicationUrlHelper;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private QuestionRestService questionRestService;


    @Test
    public void questionUrlForNoQuestionType() {
        // given
        QuestionSetupType questionSetupType = null;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void questionUrlForApplicationDetails() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.APPLICATION_DETAILS;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/application-details"), result);
    }

    @Test
    public void questionUrlForGrantAgreement() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.GRANT_AGREEMENT;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/grant-agreement"), result);
    }

    @Test
    public void questionUrlForGrantTransferDetails() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.GRANT_TRANSFER_DETAILS;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/grant-transfer-details"), result);
    }

    @Test
    public void questionUrlForApplicationTeam() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.APPLICATION_TEAM;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/team"), result);
    }

    @Test
    public void questionUrlForTermsAndConditions() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.TERMS_AND_CONDITIONS;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/terms-and-conditions/organisation/3/question/1"), result);
    }

    @Test
    public void questionUrlForResearchCategory() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.RESEARCH_CATEGORY;

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/research-category"), result);
    }

    @Test
    public void questionUrlForEqualityDiversityInclusion() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION;
        when(questionRestService.findById(QUESTION_ID)).thenReturn(restSuccess(newQuestionResource().build()));

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/question/1/generic"), result);
    }

    @Test
    public void questionUrlForNiDeclaration() {
        // given
        QuestionSetupType questionSetupType = QuestionSetupType.NORTHERN_IRELAND_DECLARATION;
        when(questionRestService.findById(QUESTION_ID)).thenReturn(restSuccess(newQuestionResource().withMultipleStatuses(true).build()));

        // when
        Optional<String> result = applicationUrlHelper.getQuestionUrl(questionSetupType, QUESTION_ID, APPLICATION_ID, ORGANISATION_ID);

        // then
        assertEquals(Optional.of("/application/2/form/organisation/3/question/1/generic"), result);
    }
}
