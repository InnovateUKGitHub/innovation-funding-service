package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.transactional.ApplicationResearchCategoryService;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Controller that exposes functionality for linking an {@link Application} to an {@link ResearchCategory}.
 */
@RestController
@RequestMapping("/applicationResearchCategory")
public class ApplicationResearchCategoryController {

    private ApplicationResearchCategoryService applicationResearchCategoryService;
    private QuestionService questionService;
    private QuestionStatusService questionStatusService;

    public ApplicationResearchCategoryController() {

    }

    public ApplicationResearchCategoryController(final ApplicationResearchCategoryService
                                                         applicationResearchCategoryService,
                                                 final QuestionService questionService,
                                                 final QuestionStatusService questionStatusService) {
        this.applicationResearchCategoryService = applicationResearchCategoryService;
        this.questionService = questionService;
        this.questionStatusService = questionStatusService;
    }

    @PostMapping("/researchCategory/{applicationId}")
    public RestResult<ApplicationResource> setResearchCategory(@PathVariable("applicationId") final long applicationId,
                                                               @RequestBody long researchCategoryId) {
        return applicationResearchCategoryService.setResearchCategory(applicationId, researchCategoryId)
                .toGetResponse();
    }

    @PostMapping("/markResearchCategoryComplete/{applicationId}/{markedAsCompleteById}")
    public RestResult<ApplicationResource> setResearchCategoryAndMarkAsComplete(@PathVariable("applicationId") long
                                                                                        applicationId,
                                                                                @PathVariable("markedAsCompleteById")
                                                                                        long markedAsCompleteById,
                                                                                @RequestBody long researchCategoryId) {
        return applicationResearchCategoryService
                .setResearchCategory(applicationId, researchCategoryId).andOnSuccessReturn(application -> {
                    markAsComplete(application, markedAsCompleteById);
                    return application;
                }).toGetResponse();
    }

    private ServiceResult<List<ValidationMessages>> markAsComplete(ApplicationResource applicationResource,
                                                                   long markedAsCompleteById) {
        return questionService.getQuestionByCompetitionIdAndQuestionSetupType(applicationResource.getCompetition(),
                RESEARCH_CATEGORY).andOnSuccess(question -> questionStatusService.markAsComplete(
                new QuestionApplicationCompositeId(question.getId(), applicationResource.getId()),
                markedAsCompleteById));
    }
}
