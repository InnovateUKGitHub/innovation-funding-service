package org.innovateuk.ifs.questionnaire.controller;


import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.innovateuk.ifs.questionnaire.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.service.QuestionnaireRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/questionnaire")
@PreAuthorize("permitAll")
public class QuestionnaireWebController {

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @GetMapping("/{questionnaireId}")
    public String welcomeScreen(Model model,
                                HttpServletRequest request,
                                UserResource user,
                                @PathVariable long questionnaireId) {
        QuestionnaireResource questionnaire = questionnaireRestService.get(questionnaireId).getSuccess();
        return "questionnaire/welcome";
    }

    @PostMapping("/{questionnaireId}")
    public String start(Model model,
                        HttpServletRequest request,
                        UserResource user,
                        @PathVariable long questionnaireId) {
        QuestionnaireResource questionnaire = questionnaireRestService.get(questionnaireId).getSuccess();
        return String.format("redirect:/questionnaire/%d/question/%d", questionnaireId, questionnaire.getQuestions().get(0));
    }

    @GetMapping("/{questionnaireId}/question/{questionId}")
    public String question(Model model,
                           HttpServletRequest request,
                           UserResource user,
                           @PathVariable long questionId) {
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        List<QuestionnaireOptionResource> options = questionnaireOptionRestService.get(question.getOptions()).getSuccess();

        model.addAttribute("question", question);
        model.addAttribute("options", options);
        return "questionnaire/question";

    }

}
