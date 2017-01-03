package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class ApplicationNavigationPopulator {
    public static final String SECTION_URL = "/section/";
    public static final String QUESTION_URL = "/question/";
    public static final String APPLICATION_BASE_URL = "/application/";

    private static final String REFERER = "referer";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    public void addNavigation(SectionResource section, Long applicationId, Model model) {
        if (section == null) {
            return;
        }
        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    public void addNavigation(QuestionResource question, Long applicationId, Model model) {
        if (question == null) {
            return;
        }

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    protected void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId, Model model) {
        String previousUrl;
        String previousText;

        if (previousQuestionOptional.isPresent()) {
            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }
            model.addAttribute("previousUrl", previousUrl);
            model.addAttribute("previousText", previousText);
        }
    }

    protected void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, Long applicationId, Model model) {
        String nextUrl;
        String nextText;

        if (nextQuestionOptional.isPresent()) {
            QuestionResource nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            model.addAttribute("nextUrl", nextUrl);
            model.addAttribute("nextText", nextText);
        }
    }

    /**
     * This method creates a URL looking at referer in request.  Because 'back' will be different depending on
     * whether the user arrived at this page via PS pages and summary vs App pages input form/overview. (INFUND-6892)
     */
    public void addAppropriateBackURLToModel(Long appId, HttpServletRequest request, Model model){
        String backURL;
        String referer = request.getHeader(REFERER);

        if(referer != null && referer.contains("/application/" + appId + "/summary")){
            backURL = referer;
        } else {
            backURL = "/application/" + appId;
        }
        model.addAttribute("backURL", backURL);
    }
}
