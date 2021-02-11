package org.innovateuk.ifs.questionnaire.populator;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseRestService;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.questionnaire.viewmodel.PreviousQuestionViewModel;
import org.innovateuk.ifs.questionnaire.viewmodel.QuestionnaireQuestionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionnaireQuestionViewModelPopulator {

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireResponseRestService questionnaireResponseRestService;

    @Autowired
    private QuestionnaireQuestionResponseRestService questionnaireQuestionResponseRestService;


    public QuestionnaireQuestionViewModel populate(long questionnaireResponseId, long questionId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        List<QuestionnaireOptionResource> options = questionnaireOptionRestService.get(question.getOptions()).getSuccess();


        return new QuestionnaireQuestionViewModel(questionnaireResponseId, question, options, previousQuestions(response, question));
    }

    private List<PreviousQuestionViewModel> previousQuestions(QuestionnaireResponseResource response, QuestionnaireQuestionResource question) {
        List<QuestionnaireQuestionResponseResource> responses = questionnaireQuestionResponseRestService.get(response.getQuestionnaireQuestionResponse()).getSuccess();
        List<QuestionnaireQuestionResource> respondedQuestions = questionnaireQuestionRestService.get(responses.stream().map(QuestionnaireQuestionResponseResource::getQuestion).collect(Collectors.toList())).getSuccess();
        List<QuestionnaireOptionResource> respondedOptions = questionnaireOptionRestService.get(responses.stream().map(QuestionnaireQuestionResponseResource::getOption).collect(Collectors.toList())).getSuccess();

        return respondedQuestions.stream()
                .filter(q -> q.getDepth() < question.getDepth())
                .map(q -> {
                    QuestionnaireQuestionResponseResource questionResponse = responses.stream().filter(r -> r.getQuestion().equals(q.getId())).findAny().orElseThrow(ObjectNotFoundException::new);
                    QuestionnaireOptionResource selectedOption = respondedOptions.stream().filter(r -> r.getId().equals(questionResponse.getOption())).findAny().orElseThrow(ObjectNotFoundException::new);
                    return new PreviousQuestionViewModel(response.getId(), q.getQuestion(), selectedOption.getText());
                })
                .collect(Collectors.toList());
    }
}
