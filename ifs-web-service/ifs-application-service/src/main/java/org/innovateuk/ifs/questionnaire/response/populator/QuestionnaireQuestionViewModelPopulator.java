package org.innovateuk.ifs.questionnaire.response.populator;

import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseRestService;
import org.innovateuk.ifs.questionnaire.response.viewmodel.QuestionnaireQuestionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private AnsweredQuestionViewModelPopulator answeredQuestionViewModelPopulator;

    @Autowired
    private QuestionnaireResponseLinkRestService linkRestService;

    public QuestionnaireQuestionViewModel populate(String questionnaireResponseId, long questionId) {
        QuestionnaireResponseResource response = questionnaireResponseRestService.get(questionnaireResponseId).getSuccess();
        QuestionnaireResource questionnaire = questionnaireRestService.get(response.getQuestionnaire()).getSuccess();
        String subtitle = "";
        if (questionnaire.getSecurityType() == QuestionnaireSecurityType.LINK) {
            QuestionnaireLinkResource link = linkRestService.getQuestionnaireLink(questionnaireResponseId).getSuccess();
            if (link instanceof ApplicationOrganisationLinkResource) {
                subtitle = ((ApplicationOrganisationLinkResource) link).getApplicationName();
            }
        }
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        List<QuestionnaireOptionResource> options = questionnaireOptionRestService.get(question.getOptions()).getSuccess();
        return new QuestionnaireQuestionViewModel(questionnaireResponseId, subtitle, question, options, answeredQuestionViewModelPopulator.answersBeforeQuestion(response, question));
    }

}
