package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireOption;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireOptionRepository;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireQuestionRepository;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireTextOutcomeRepository;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.questionnaire.builder.QuestionnaireOptionResourceBuilder.newQuestionnaireOptionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireOptionServiceImplTest {

    @InjectMocks
    private QuestionnaireOptionServiceImpl service;

    @Mock
    private QuestionnaireOptionRepository questionnaireOptionRepository;

    @Mock
    private QuestionnaireQuestionRepository questionnaireQuestionRepository;

    @Mock
    private QuestionnaireTextOutcomeRepository questionnaireTextOutcomeRepository;

    @Test
    public void mapToDomain() {
        long questionId = 1L;
        long questionDecisionId = 99L;
        QuestionnaireOptionResource optionResource = newQuestionnaireOptionResource()
                .withDecision(questionDecisionId)
                .withDecisionType(DecisionType.QUESTION)
                .withText("Choice")
                .withQuestion(questionId)
                .build();
        QuestionnaireOption optionDomain = new QuestionnaireOption();
        QuestionnaireQuestion previousQuestion1 = new QuestionnaireQuestion();
        QuestionnaireOption previousOption1 = new QuestionnaireOption();
        previousOption1.setQuestion(previousQuestion1);
        previousQuestion1.setDepth(3);
        QuestionnaireQuestion previousQuestion2 = new QuestionnaireQuestion();
        QuestionnaireOption previousOption2 = new QuestionnaireOption();
        previousOption2.setQuestion(previousQuestion2);
        previousQuestion2.setDepth(5);
        QuestionnaireQuestion question = new QuestionnaireQuestion();
        question.setOptionsLinkedToThisDecision(newArrayList(previousOption1, previousOption2));
        QuestionnaireQuestion decisionQuestion = new QuestionnaireQuestion();
        when(questionnaireQuestionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionnaireQuestionRepository.findById(questionDecisionId)).thenReturn(Optional.of(decisionQuestion));

        service.mapToDomain(optionDomain, optionResource);

        assertThat(question, is(optionDomain.getQuestion()));
        assertThat(decisionQuestion, is(optionDomain.getDecision()));
        assertThat(optionResource.getText(), is(equalTo(optionDomain.getText())));
    }
}