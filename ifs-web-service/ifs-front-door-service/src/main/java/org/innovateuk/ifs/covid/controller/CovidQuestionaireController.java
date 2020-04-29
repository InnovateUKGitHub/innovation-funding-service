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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
                       @Valid @ModelAttribute("form") CovidQuestionaireForm webForm,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("questionType", questionType);
            model.addAttribute("previousAnswers", getPreviousAnswers(questionType));
        }
        return getNextPage(questionType, model, webForm.getAnswer());
    }

    private String getNextPage(CovidQuestionnaireType type, Model model, boolean answer) {
        switch (type) {
            case BUSINESS:
                if (answer) {
                    return redirectToQuestion(AWARD_RECIPIENT);
                } else {
                    return decision(model, "non-business");
                }
            case AWARD_RECIPIENT:
                if (answer) {
                    return redirectToQuestion(CHALLENGE_TIMING);
                } else {
                    return decision(model, "default");
                }
            case CHALLENGE_TIMING:
                if (answer) {
                    return decision(model, "contact-monitoring-officer");
                } else {
                    return redirectToQuestion(CHALLENGE_CASHFLOW);
                }
            case CHALLENGE_CASHFLOW:
                if (answer) {
                    return decision(model, "monthly-funding");
                } else {
                    return redirectToQuestion(CHALLENGE_LARGE_FUNDING_GAP);
                }
            case CHALLENGE_LARGE_FUNDING_GAP:
                if (answer) {
                    return redirectToQuestion(CHALLENGE_ELIGIBILTY);
                } else {
                    return redirectToQuestion(CHALLENGE_SIGNIFICANT_FUNDING_GAP);
                }
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                if (answer) {
                    return decision(model, "continuity-loan");
                } else {
                    return decision(model, "default");
                }
            case CHALLENGE_ELIGIBILTY:
                if (answer) {
                    return decision(model, "continuity-grant");
                } else {
                    return decision(model, "default");
                }
        }
        throw new IFSRuntimeException("Unkown question type");
    }

    private String decision(Model model, String decision) {
        model.addAttribute("decision", decision);
        return "covid/questionnaire";
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
            case CHALLENGE_ELIGIBILTY:
                return combineLists(getPreviousAnswers(CHALLENGE_LARGE_FUNDING_GAP), Pair.of(CHALLENGE_LARGE_FUNDING_GAP, false));
        }
        throw new IFSRuntimeException("Unkown question type");
    }

    private String redirectToQuestion(CovidQuestionnaireType type) {
        return "redirect:/covid-19/questionnaire/" + type.getUrl();
    }

}
