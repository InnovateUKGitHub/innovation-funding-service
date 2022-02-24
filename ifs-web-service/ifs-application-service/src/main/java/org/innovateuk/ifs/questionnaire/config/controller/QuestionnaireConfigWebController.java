package org.innovateuk.ifs.questionnaire.config.controller;


import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.questionnaire.config.form.QuestionnaireQuestionConfigForm;
import org.innovateuk.ifs.questionnaire.config.form.QuestionnaireQuestionOptionForm;
import org.innovateuk.ifs.questionnaire.config.populator.QuestionnaireConfigViewModelPopulator;
import org.innovateuk.ifs.questionnaire.config.populator.QuestionnaireQuestionConfigFormPopulator;
import org.innovateuk.ifs.questionnaire.config.populator.QuestionnaireQuestionConfigViewModelPopulator;
import org.innovateuk.ifs.questionnaire.config.saver.QuestionnaireQuestionConfigFormSaver;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.viewmodel.QuestionnaireQuestionConfigViewModel;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Controller
@RequestMapping("/questionnaire-configure")
@PreAuthorize("permitAll")
@Profile("questionnaire-configure")
public class QuestionnaireConfigWebController {

    @Autowired
    private QuestionnaireConfigViewModelPopulator populator;

    @Autowired
    private QuestionnaireQuestionConfigFormPopulator questionnaireQuestionConfigFormPopulator;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireQuestionConfigViewModelPopulator questionnaireQuestionConfigViewModelPopulator;

    @Autowired
    private QuestionnaireQuestionConfigFormSaver questionnaireQuestionConfigFormSaver;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @GetMapping("/{questionnaireId}")
    public String questions(Model model,
                            HttpServletRequest request,
                            UserResource user,
                            @PathVariable long questionnaireId) {
        model.addAttribute("model", populator.populate(questionnaireId));

        return "questionnaire/questionnaire-configure";
    }

    @PostMapping("/{questionnaireId}")
    public String newPost(Model model,
                          HttpServletRequest request,
                          UserResource user,
                          @PathVariable long questionnaireId) {
        return String.format("redirect:/questionnaire-configure/%d/question/new", questionnaireId);
    }

    @PostMapping(value = "/{questionnaireId}", params = "delete-question")
    public String newPost(Model model,
                          HttpServletRequest request,
                          UserResource user,
                          @PathVariable long questionnaireId,
                          @RequestParam("delete-question") long deleteId) {
        questionnaireQuestionRestService.delete(deleteId).getSuccess();
        return String.format("redirect:/questionnaire-configure/%d", questionnaireId);
    }

    @GetMapping("/{questionnaireId}/question/new")
    public String create(@ModelAttribute(name = "form", binding = false) QuestionnaireQuestionConfigForm form,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model,
                         HttpServletRequest request,
                         UserResource user,
                         @PathVariable long questionnaireId) {
        model.addAttribute("model", QuestionnaireQuestionConfigViewModel.aCreateViewModel(questionnaireId));
        return "questionnaire/questionnaire-question-configure";
    }

    @PostMapping("/{questionnaireId}/question/new")
    public String create(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
                         Model model,
                         HttpServletRequest request,
                         UserResource user,
                         @PathVariable long questionnaireId) {
        questionnaireQuestionConfigFormSaver.create(questionnaireId, form);
        return String.format("redirect:/questionnaire-configure/%d", questionnaireId);
    }

    @GetMapping("/{questionnaireId}/question/{questionId}")
    public String edit(
                         Model model,
                         HttpServletRequest request,
                         UserResource user,
                         @PathVariable long questionnaireId,
                       @PathVariable long questionId) {
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        model.addAttribute("model", questionnaireQuestionConfigViewModelPopulator.populate(questionnaireId, question));
        model.addAttribute("form", questionnaireQuestionConfigFormPopulator.form(questionnaireId, question));
        return "questionnaire/questionnaire-question-configure";
    }

    @PostMapping(value = "/{questionnaireId}/question/{questionId}", params = "add-option-question")
    public String addQuestionOption(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
                                    Model model,
                                    HttpServletRequest request,
                                    UserResource user,
                                    @PathVariable long questionnaireId,
                                    @PathVariable long questionId) {
        QuestionnaireQuestionOptionForm option = new QuestionnaireQuestionOptionForm();
        option.setDecisionType(DecisionType.QUESTION);
        form.getOptions().add(option);
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        model.addAttribute("model", questionnaireQuestionConfigViewModelPopulator.populate(questionnaireId, question));
        return "questionnaire/questionnaire-question-configure";
    }

    @PostMapping(value = "/{questionnaireId}/question/{questionId}", params = "add-option-text")
    public String addTextOption(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
                                    Model model,
                                    HttpServletRequest request,
                                    UserResource user,
                                    @PathVariable long questionnaireId,
                                    @PathVariable long questionId) {
        QuestionnaireQuestionOptionForm option = new QuestionnaireQuestionOptionForm();
        option.setDecisionType(DecisionType.TEXT_OUTCOME);
        form.getOptions().add(option);
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        model.addAttribute("model", questionnaireQuestionConfigViewModelPopulator.populate(questionnaireId, question));
        return "questionnaire/questionnaire-question-configure";
    }

    @PostMapping(value = "/{questionnaireId}/question/{questionId}", params = "remove-option")
    public String removeOption(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
                                Model model,
                                HttpServletRequest request,
                                UserResource user,
                                @PathVariable long questionnaireId,
                                @PathVariable long questionId,
                                @RequestParam("remove-option") int indexToRemove) {
        form.getOptions().remove(indexToRemove);
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        model.addAttribute("model", questionnaireQuestionConfigViewModelPopulator.populate(questionnaireId, question));
        return "questionnaire/questionnaire-question-configure";
    }

    @PostMapping(value = "/{questionnaireId}/question/{questionId}", params = "delete-option")
    public String deleteOption(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
                               Model model,
                               HttpServletRequest request,
                               UserResource user,
                               @PathVariable long questionnaireId,
                               @PathVariable long questionId,
                               @RequestParam("delete-option") long deleteId) {
        form.getOptions().removeIf(o -> Objects.equals(o.getOptionId(), deleteId));
        questionnaireOptionRestService.delete(deleteId).getSuccess();
        return String.format("redirect:/questionnaire-configure/%d/question/%d", questionnaireId, questionId);
    }

    @PostMapping("/{questionnaireId}/question/{questionId}")
    public String edit(@ModelAttribute(name = "form") QuestionnaireQuestionConfigForm form,
            Model model,
            HttpServletRequest request,
            UserResource user,
            @PathVariable long questionnaireId,
            @PathVariable long questionId) {
        questionnaireQuestionConfigFormSaver.edit(questionnaireId, questionId, form);
        return String.format("redirect:/questionnaire-configure/%d", questionnaireId);
    }

}
