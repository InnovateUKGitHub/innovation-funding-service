package org.innovateuk.ifs.covid.controller;


import org.apache.commons.lang.BooleanUtils;
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
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.covid.CovidQuestionnaireType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

@Controller
@RequestMapping("/covid-19/questionnaire")
@SecuredBySpring(value = "Controller", description = "Anyone can do covid questionnaire", securedType = CompetitionController.class)
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
            return "covid/questionnaire";
        }
        return "redirect:/covid-19/questionnaire/" + questionType.getUrl() + "/" + BooleanUtils.toStringYesNo(webForm.getAnswer());
    }

    @GetMapping("/{questionType}/{yesNo}")
    public String form(@PathVariable CovidQuestionnaireType questionType,
                       @PathVariable String yesNo,
                       Model model) {
        boolean answer = BooleanUtils.toBoolean(yesNo);
        return getNextPage(questionType, model, answer);
    }

    private String getNextPage(CovidQuestionnaireType type, Model model, boolean answer) {
        switch (type) {
            case BUSINESS:
                if (answer) {
                    return redirectToQuestion(AWARD_RECIPIENT);
                } else {
                    return decision(model, type, answer, "non-business");
                }
            case AWARD_RECIPIENT:
                if (answer) {
                    return redirectToQuestion(CHALLENGE_TIMING);
                } else {
                    return decision(model, type, answer, "default");
                }
            case CHALLENGE_TIMING:
                if (answer) {
                    return decision(model, type, answer, "contact-monitoring-officer");
                } else {
                    return redirectToQuestion(CHALLENGE_CASHFLOW);
                }
            case CHALLENGE_CASHFLOW:
                if (answer) {
                    return decision(model, type, answer, "monthly-funding");
                } else {
                    return redirectToQuestion(CHALLENGE_SIGNIFICANT_FUNDING_GAP);
                }
//            case CHALLENGE_LARGE_FUNDING_GAP:
//                if (answer) {
//                    return decision(model, type, answer, "continuity-grant");
//                } else {
//                    return redirectToQuestion(CHALLENGE_SIGNIFICANT_FUNDING_GAP);
//                }
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                if (answer) {
                    return decision(model, type, answer, "continuity-loan");
                } else {
                    return decision(model, type, answer, "default");
                }
        }
        throw new IFSRuntimeException("Unknown question type");
    }

    private String decision(Model model, CovidQuestionnaireType type, boolean answer, String decision) {
        List<Pair<CovidQuestionnaireType, Boolean>> previousAnswers = getPreviousAnswers(type);
        previousAnswers.add(Pair.of(type, answer));
        model.addAttribute("previousAnswers", previousAnswers);
        model.addAttribute("decision", decision);
        model.addAttribute("form", new CovidQuestionaireForm());
        return "covid/questionnaire";
    }

    private List<Pair<CovidQuestionnaireType, Boolean>> getPreviousAnswers(CovidQuestionnaireType type) {
        switch(type) {
            case BUSINESS:
                return new ArrayList<>();
            case AWARD_RECIPIENT:
                return newArrayList(Pair.of(BUSINESS, true));
            case CHALLENGE_TIMING:
                return combineLists(getPreviousAnswers(AWARD_RECIPIENT), Pair.of(AWARD_RECIPIENT, true));
            case CHALLENGE_CASHFLOW:
                return combineLists(getPreviousAnswers(CHALLENGE_TIMING), Pair.of(CHALLENGE_TIMING, false));
//            case CHALLENGE_LARGE_FUNDING_GAP:
//                return combineLists(getPreviousAnswers(CHALLENGE_CASHFLOW), Pair.of(CHALLENGE_CASHFLOW, false));
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                return combineLists(getPreviousAnswers(CHALLENGE_CASHFLOW), Pair.of(CHALLENGE_CASHFLOW, false));
        }
        throw new IFSRuntimeException("Unknown question type");
    }

    private String redirectToQuestion(CovidQuestionnaireType type) {
        return "redirect:/covid-19/questionnaire/" + type.getUrl();
    }

}
