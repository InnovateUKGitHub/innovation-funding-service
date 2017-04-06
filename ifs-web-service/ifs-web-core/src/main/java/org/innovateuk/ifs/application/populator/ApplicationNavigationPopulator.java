package org.innovateuk.ifs.application.populator;

import com.google.common.collect.Iterables;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    private ApplicationService applicationService;

    public NavigationViewModel addNavigation(SectionResource section, Long applicationId) {

        return addNavigation(section, applicationId, null);
    }

    public NavigationViewModel addNavigation(SectionResource section, Long applicationId, List<SectionType> sectionTypesToSkip) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (section == null) {
            return navigationViewModel;
        }
        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel, sectionTypesToSkip);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel, sectionTypesToSkip);

        return navigationViewModel;
    }

    public NavigationViewModel addNavigation(QuestionResource question, Long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (question == null) {
            return navigationViewModel;
        }

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel, null);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel, null);

        return navigationViewModel;
    }

    protected void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId,
                                              NavigationViewModel navigationViewModel, List<SectionType> sectionTypesToSkip) {
        while (previousQuestionOptional.isPresent()) {
            String previousUrl;
            String previousText;

            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());

            if (sectionTypesToSkip != null && sectionTypesToSkip.contains(previousSection.getType())) {
                previousQuestionOptional = questionService.getPreviousQuestion(previousSection.getQuestions().get(0));
            } else {

                if (previousSection.isQuestionGroup()) {
                    previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                    previousText = previousSection.getName();
                } else {
                    previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                    previousText = previousQuestion.getShortName();
                }

                navigationViewModel.setPreviousUrl(previousUrl);
                navigationViewModel.setPreviousText(previousText);
                break;
            }
        }
    }

    protected void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, Long applicationId,
                                          NavigationViewModel navigationViewModel,  List<SectionType> sectionTypesToSkip) {
        while (nextQuestionOptional.isPresent()) {
            String nextUrl;
            String nextText;

            QuestionResource nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (sectionTypesToSkip != null && sectionTypesToSkip.contains(nextSection.getType())) {
                Long lastQuestion = Iterables.getLast(nextSection.getQuestions());
                nextQuestionOptional = questionService.getNextQuestion(lastQuestion);
            } else {

                if (nextSection.isQuestionGroup()) {
                    nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                    nextText = nextSection.getName();
                } else {
                    nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                    nextText = nextQuestion.getShortName();
                }

                navigationViewModel.setNextUrl(nextUrl);
                navigationViewModel.setNextText(nextText);
                break;
            }
        }
    }

    /**
     * This method creates a URL looking at referer in request.  Because 'back' will be different depending on
     * whether the user arrived at this page via PS pages and summary vs App pages input form/overview. (INFUND-6892)
     */
    public void addAppropriateBackURLToModel(Long applicationId, Model model, SectionResource section){
        if (section != null && SectionType.FINANCE.equals(section.getType().getParent().orElse(null))) {
            model.addAttribute("backTitle", "Your finances");
            model.addAttribute("backURL", "/application/" + applicationId + "/form/" + SectionType.FINANCE.name());
        } else {
            ApplicationResource application = applicationService.getById(applicationId);
            String backURL = "/application/" + applicationId;

            if(!application.isOpen() || !application.getCompetitionStatus().equals(CompetitionStatus.OPEN)){
                model.addAttribute("backTitle", "Application summary");
                backURL += "/summary";
            } else {
                model.addAttribute("backTitle", "Application Overview");
            }

            model.addAttribute("backURL", backURL);
        }
    }
}
