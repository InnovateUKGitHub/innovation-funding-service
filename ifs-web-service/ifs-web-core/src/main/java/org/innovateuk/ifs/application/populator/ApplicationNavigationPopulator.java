package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
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

    public NavigationViewModel addNavigation(SectionResource section, Long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (section == null) {
            return navigationViewModel;
        }
        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel);

        return navigationViewModel;
    }

    public NavigationViewModel addNavigation(QuestionResource question, Long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();
        if (question == null) {
            return navigationViewModel;
        }

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel);

        return navigationViewModel;
    }

    protected void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId, NavigationViewModel navigationViewModel) {
        if (previousQuestionOptional.isPresent()) {
            String previousUrl;
            String previousText;

            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }

            navigationViewModel.setPreviousUrl(previousUrl);
            navigationViewModel.setPreviousText(previousText);
        }
    }

    protected void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, Long applicationId, NavigationViewModel navigationViewModel) {
        if (nextQuestionOptional.isPresent()) {
            String nextUrl;
            String nextText;

            QuestionResource nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            navigationViewModel.setNextUrl(nextUrl);
            navigationViewModel.setNextText(nextText);
        }
    }

    /**
     * This method creates a URL looking at referer in request.  Because 'back' will be different depending on
     * whether the user arrived at this page via PS pages and summary vs App pages input form/overview. (INFUND-6892)
     */
    public void addAppropriateBackURLToModel(Long appId, HttpServletRequest request, Model model, SectionResource section){
        if (section != null && SectionType.FINANCE.equals(section.getType().getParent().orElse(null))) {
            model.addAttribute("backTitle", "Your finances");
            model.addAttribute("backURL", "/application/" + appId + "/form/" + SectionType.FINANCE.name());
        } else {
            String backURL;
            String referer = request.getHeader(REFERER);

            if(referer != null && referer.contains("/application/" + appId + "/summary")){
                model.addAttribute("backTitle", "Application Summary");
                backURL = referer;
            } else {
                model.addAttribute("backTitle", "Application Overview");
                backURL = "/application/" + appId;
            }
            model.addAttribute("backURL", backURL);


        }

    }
}
