package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * This controller will handle all submit requests that are related to the application overview.
 */

@Controller
@RequestMapping("/application")
public class ApplicationSubmitController {

    private QuestionService questionService;
    private QuestionRestService questionRestService;
    private UserRestService userRestService;
    private ApplicationService applicationService;
    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private ApplicationModelPopulator applicationModelPopulator;
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    private static final String FORM_ATTR_NAME = "form";
    public static final String APPLICATION_SUBMIT_FROM_ATTR_NAME = "applicationSubmitForm";

    public ApplicationSubmitController() {
    }

    @Autowired
    public ApplicationSubmitController(QuestionService questionService,
                                       QuestionRestService questionRestService,
                                       UserRestService userRestService,
                                       ApplicationService applicationService,
                                       ApplicationRestService applicationRestService,
                                       CompetitionRestService competitionRestService,
                                       ApplicationModelPopulator applicationModelPopulator,
                                       CookieFlashMessageFilter cookieFlashMessageFilter) {
        this.questionService = questionService;
        this.questionRestService = questionRestService;
        this.userRestService = userRestService;
        this.applicationService = applicationService;
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.applicationModelPopulator = applicationModelPopulator;
        this.cookieFlashMessageFilter = cookieFlashMessageFilter;
    }


    private boolean isResearchCategoryQuestion(Long questionId) {
        QuestionResource question = questionRestService.findById(questionId).getSuccess();
        return question.getQuestionSetupType() == RESEARCH_CATEGORY;
    }
}


