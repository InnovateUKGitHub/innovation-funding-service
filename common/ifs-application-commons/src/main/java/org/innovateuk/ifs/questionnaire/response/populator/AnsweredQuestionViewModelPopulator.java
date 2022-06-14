package org.innovateuk.ifs.questionnaire.response.populator;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnswerTableViewModel;
import org.innovateuk.ifs.questionnaire.response.viewmodel.AnsweredQuestionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class AnsweredQuestionViewModelPopulator {

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;

    @Autowired
    private QuestionnaireResponseRestService questionnaireResponseRestService;

    public AnswerTableViewModel allAnswers(String questionnaireResponseId, boolean readonly) {
        return allAnswers(questionnaireResponseId, "Provided answers", readonly);
    }

    public AnswerTableViewModel allAnswers(String questionnaireResponseId, String heading, boolean readonly) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        final QuestionnaireOptionResource[] selectedOption = {null};
        List<AnsweredQuestionViewModel> answers = getAnswers(response, (q) -> true, o -> selectedOption[0] = o);
        if (selectedOption[0] != null && selectedOption[0].getDecisionType() == DecisionType.QUESTION) {
            QuestionnaireQuestionResource unansweredQuestion = questionnaireQuestionRestService.get(selectedOption[0].getDecision()).getSuccess();
            answers.add(new AnsweredQuestionViewModel(unansweredQuestion.getId(), unansweredQuestion.getQuestion(), null));
        }
        return new AnswerTableViewModel(questionnaireResponseId, heading, answers, readonly);
    }

    public AnswerTableViewModel answersBeforeQuestion(QuestionnaireResponseResource response, QuestionnaireQuestionResource question) {
        return new AnswerTableViewModel(response.getId(), "Previous answers", getAnswers(response, q -> q.getDepth() < question.getDepth(), o -> {}), false);
    }

    private List<AnsweredQuestionViewModel> getAnswers(QuestionnaireResponseResource response, Predicate<QuestionnaireQuestionResource> filter, Consumer<QuestionnaireOptionResource> finalOptionConsumer) {
        List<QuestionnaireQuestionResponseResource> responses = questionnaireQuestionResponseRestService.get(response.getQuestionnaireQuestionResponse()).getSuccess();
        List<QuestionnaireQuestionResource> respondedQuestions = questionnaireQuestionRestService.get(responses.stream().map(QuestionnaireQuestionResponseResource::getQuestion).collect(Collectors.toList())).getSuccess();
        List<QuestionnaireOptionResource> respondedOptions = questionnaireOptionRestService.get(responses.stream().map(QuestionnaireQuestionResponseResource::getOption).collect(Collectors.toList())).getSuccess();

        List<AnsweredQuestionViewModel> answeredQuestions = new ArrayList<>();
        QuestionnaireOptionResource selectedOption = null;
        for (QuestionnaireQuestionResource q : respondedQuestions) {
            if (filter.test(q)) {
                QuestionnaireQuestionResponseResource questionResponse = responses.stream().filter(r -> r.getQuestion().equals(q.getId())).findAny().orElseThrow(ObjectNotFoundException::new);
                selectedOption = respondedOptions.stream().filter(r -> r.getId().equals(questionResponse.getOption())).findAny().orElseThrow(ObjectNotFoundException::new);
                answeredQuestions.add(new AnsweredQuestionViewModel(q.getId(), q.getQuestion(), selectedOption.getText()));
            }
        }
        finalOptionConsumer.accept(selectedOption);
        return answeredQuestions;

    }
}
