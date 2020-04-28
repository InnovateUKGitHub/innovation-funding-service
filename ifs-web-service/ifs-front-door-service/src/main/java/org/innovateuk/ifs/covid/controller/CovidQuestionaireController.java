package org.innovateuk.ifs.covid.controller;


import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.controller.CompetitionController;
import org.innovateuk.ifs.covid.CovidQuestionnaireType;
import org.innovateuk.ifs.covid.form.CovidQuestionaireForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.covid.CovidQuestionnaireType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

@Controller
@RequestMapping("/covid-19/questionnaire")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionController.class)
@PreAuthorize("permitAll")
public class CovidQuestionaireController {

    @GetMapping
    public String start(Model model) {
        model.addAttribute("form", new CovidQuestionaireForm());
        return "covid/questionnaire";
    }

    @GetMapping("/{questionType}")
    public String form(@PathVariable CovidQuestionnaireType questionType,
                       Model model) {
        model.addAttribute("form", new CovidQuestionaireForm());
        model.addAttribute("questionType", questionType);
        model.addAttribute("previousAnswers", getPreviousAnswers(questionType));
        return "covid/questionnaire";
    }

    @PostMapping("/{questionType}")
    public String save(@PathVariable CovidQuestionnaireType questionType,
                       @ModelAttribute("form") CovidQuestionaireForm webForm) {
        return getNextPage(questionType, webForm.getAnswer());
    }

    private String getNextPage(CovidQuestionnaireType type, boolean answer) {
        switch (type) {
            case BUSINESS:
                if (answer) {
                    return redirectToQuestion(AWARD_RECIPIENT);
                } else {
                    return "decision";
                }
            case AWARD_RECIPIENT:
                if (answer) {
                    return redirectToQuestion(CHALLENGE_TIMING);
                } else {
                    return "decision";
                }
            case CHALLENGE_TIMING:
                if (answer) {
                    return "decision";
                } else {
                    return redirectToQuestion(CHALLENGE_CASHFLOW);
                }
            case CHALLENGE_CASHFLOW:
                if (answer) {
                    return "decision";
                } else {
                    return redirectToQuestion(CHALLENGE_LARGE_FUNDING_GAP);
                }
            case CHALLENGE_LARGE_FUNDING_GAP:
                if (answer) {
                    return "decision";
                } else {
                    return redirectToQuestion(CHALLENGE_SIGNIFICANT_FUNDING_GAP);
                }
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                if (answer) {
                    return "decision";
                } else {
                    return "decision";
                }
        }
        throw new IFSRuntimeException("Unkown question type");
    }

    private List<Pair<CovidQuestionnaireType, Boolean>> getPreviousAnswers(CovidQuestionnaireType type) {
        switch(type) {
            case BUSINESS:
                return emptyList();
            case AWARD_RECIPIENT:
                return singletonList(Pair.of(BUSINESS, true));
            case CHALLENGE_TIMING:
                return combineLists(getPreviousAnswers(AWARD_RECIPIENT), Pair.of(AWARD_RECIPIENT, true));
            case CHALLENGE_CASHFLOW:
                return combineLists(getPreviousAnswers(CHALLENGE_TIMING), Pair.of(CHALLENGE_TIMING, false));
            case CHALLENGE_LARGE_FUNDING_GAP:
                return combineLists(getPreviousAnswers(CHALLENGE_CASHFLOW), Pair.of(CHALLENGE_CASHFLOW, false));
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                return combineLists(getPreviousAnswers(CHALLENGE_SIGNIFICANT_FUNDING_GAP), Pair.of(CHALLENGE_SIGNIFICANT_FUNDING_GAP, false));
        }
        throw new IFSRuntimeException("Unkown question type");
    }

    private String redirectToQuestion(CovidQuestionnaireType type) {
        return "redirect:/covid-19/questionnaire/" + type.name();
    }

}
