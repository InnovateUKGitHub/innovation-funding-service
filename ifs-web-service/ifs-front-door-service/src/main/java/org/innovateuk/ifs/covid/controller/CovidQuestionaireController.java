package org.innovateuk.ifs.covid.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.controller.CompetitionController;
import org.innovateuk.ifs.covid.CovidQuestionnaireType;
import org.innovateuk.ifs.covid.form.CovidQuestionaireForm;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.covid.CovidQuestionnaireType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

@Controller
@RequestMapping("/covid-19/questionnaire")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionController.class)
@PreAuthorize("permitAll")
public class CovidQuestionaireController {

    private static final String FORM_COOKIE = "QUESTIONAIRE_COOKIE";
    @Autowired
    private EncryptedCookieService encryptedCookieService;

    @GetMapping
    public String start(Model model, HttpServletResponse response) {
        model.addAttribute("form", new CovidQuestionaireForm());
        return "covid/questionnaire";
    }

    @GetMapping("/{questionType}")
    public String form(@PathVariable CovidQuestionnaireType questionType,
                       Model model,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        model.addAttribute("form", form);
        model.addAttribute("questionType", questionType);
        return "covid/questionnaire";
    }

    @PostMapping("/{questionType}")
    public String save(@PathVariable CovidQuestionnaireType questionType,
                       @ModelAttribute("form") CovidQuestionaireForm webForm,
                       Model model,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        CovidQuestionaireForm cookieForm = getFormFromCookie(request);
        questionType.setValue(cookieForm, questionType.getValue(webForm));
        saveCookieForm(response, cookieForm);
        if (cookieForm.isDecisionMade()) {
            model.addAttribute("questionType", questionType);
            return "covid/questionnaire";
        } else {
            return "redirect:/covid-19/questionnaire/" + CovidQuestionnaireType.values()[questionType.ordinal() + 1].name();
        }
    }

    private List<Pair<CovidQuestionnaireType, Boolean>> getPreviousQuestions(CovidQuestionnaireType type) {
        switch(type) {
            case BUSINESS:
                return emptyList();
            case AWARD_RECIPIENT:
                return singletonList(Pair.of(BUSINESS, true));
            case CHALLENGE_TIMING:
                return combineLists(getPreviousQuestions(AWARD_RECIPIENT), Pair.of(AWARD_RECIPIENT, true));
            case CHALLENGE_CASHFLOW:
                return combineLists(getPreviousQuestions(CHALLENGE_TIMING), Pair.of(CHALLENGE_TIMING, false));
            case CHALLENGE_LARGE_FUNDING_GAP:
                return asList(BUSINESS, AWARD_RECIPIENT, CHALLENGE_TIMING, CHALLENGE_CASHFLOW);
            case CHALLENGE_SIGNIFICANT_FUNDING_GAP:
                return asList(BUSINESS, AWARD_RECIPIENT, CHALLENGE_TIMING, CHALLENGE_CASHFLOW, CHALLENGE_LARGE_FUNDING_GAP);
        }
    }


    private void saveCookieForm(HttpServletResponse response, CovidQuestionaireForm cookieForm) {
        encryptedCookieService.saveToCookie(response, FORM_COOKIE, JsonUtil.getSerializedObject(cookieForm));
    }
    private CovidQuestionaireForm getFormFromCookie(HttpServletRequest request) {
        return encryptedCookieService.getCookieAs(request, FORM_COOKIE, new TypeReference<CovidQuestionaireForm>() {})
                .orElse(new CovidQuestionaireForm());
    }
}
