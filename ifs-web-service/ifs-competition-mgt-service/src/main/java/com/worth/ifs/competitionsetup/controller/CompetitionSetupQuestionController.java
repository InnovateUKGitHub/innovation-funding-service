package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.model.Question;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.isSendToDashboard;

/**
 * Controller for showing and handling the different competition setup application question section
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/application/question")
public class CompetitionSetupQuestionController {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionController.class);

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
    public String seeQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         Model model) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
            LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        setupQuestionToModel(competition, questionId, model);
        model.addAttribute("editable", false);

        return "competition/setup/question";
    }

    @RequestMapping(value = "/{questionId}/edit", method = RequestMethod.GET)
    public String editQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                          @PathVariable("questionId") Long questionId,
                                          Model model) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
            LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        setupQuestionToModel(competition, questionId, model);
        model.addAttribute("editable", true);

        return "competition/setup/question";
    }

    @RequestMapping(value = "/{questionId}", method = RequestMethod.POST)
    public String submitApplicationQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationFormForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            @PathVariable("questionId") Long questionId,
                                            Model model) {

        competitionSetupQuestionService.updateQuestion(competitionSetupForm.getQuestionToUpdate());

        if(!bindingResult.hasErrors()) {
            return "redirect:/competition/setup/" + competitionId + "/section/application";
        } else {
            competitionSetupService.populateCompetitionSectionModelAttributes(model, competitionService.getById(competitionId), CompetitionSetupSection.APPLICATION_FORM);
            addQuestionToUpdate(questionId, model, competitionSetupForm);
            model.addAttribute("competitionSetupForm", competitionSetupForm);
            return "competition/setup/question";
        }
    }

    private void setupQuestionToModel(final CompetitionResource competition, final Long questionId, Model model) {
        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;

        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        ApplicationFormForm competitionSetupForm = (ApplicationFormForm) competitionSetupService.getSectionFormData(competition, section);

        addQuestionToUpdate(questionId, model, competitionSetupForm);
        model.addAttribute("competitionSetupForm", competitionSetupForm);
    }

    private void addQuestionToUpdate(Long questionId, Model model, ApplicationFormForm applicationFormForm) {
        if(model.containsAttribute("questions")) {
            List<Question> questions = (List<Question>) model.asMap().get("questions");
            Optional<Question> question = questions.stream().filter(questionObject -> questionObject.getId().equals(questionId)).findFirst();
            if(question.isPresent()) {
                applicationFormForm.setQuestionToUpdate(question.get());
            } else {
                LOG.error("Question(" + questionId + ") not found");
            }
        }
    }
}
