package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.form.AssessmentSummaryForm;
import com.worth.ifs.assessment.model.AssessmentSummaryModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.workflow.ProcessOutcomeService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.toField;

@Controller
public class AssessmentSummaryController extends AbstractApplicationController {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessOutcomeService processOutcomeService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    private static String SUMMARY = "assessment/application-summary";

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.GET)
    public String getSummary(Model model,
                             HttpServletResponse response,
                             @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentSummaryForm form,
                             BindingResult bindingResult, @PathVariable("assessmentId") Long assessmentId) throws ExecutionException, InterruptedException {
        populateFormWithExistingValues(form, assessmentId);
        model.addAttribute("model", assessmentSummaryModelPopulator.populateModel(assessmentId));
        return SUMMARY;
    }

    @RequestMapping(value = "/{assessmentId}/summary", method = RequestMethod.POST)
    public String save(Model model,
                       HttpServletResponse response,
                       @Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) AssessmentSummaryForm form,
                       BindingResult bindingResult,
                       ValidationHandler validationHandler,
                       @PathVariable("assessmentId") Long assessmentId) {

        //TODO change implementation of lambda call to handle exceptions concisely
        Supplier<String> failureView = () -> {
            String view = "";
            try {
                view = getSummary(model, response, form, bindingResult, assessmentId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return view;
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = assessmentService.recommend(assessmentId, form.getFundingConfirmation(), form.getFeedback(), form.getComment());
            validationHandler.addAnyErrors(updateResult, toField("formErrors"));
            //TODO change implementation of lambda call to handle exceptions concisely
            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> {
                        String view = "";
                        try {
                            view = redirectToCompetitionOfAssessment(assessmentId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return view;
                    });
        });
    }

    private String redirectToCompetitionOfAssessment(Long assessmentId) throws InterruptedException, ExecutionException {
        CompetitionResource competition = getCompetition(getApplicationForAssessment(assessmentId).getCompetition());
        return "redirect:/assessor/dashboard/competition/" + competition.getId();
    }

    private void populateFormWithExistingValues(AssessmentSummaryForm form, Long assessmentId) {
        getOutcome(assessmentId).ifPresent(outcome -> {
            form.setFundingConfirmation(Optional.ofNullable(outcome.getOutcome()).map(BooleanUtils::toBoolean).orElse(null));
            form.setFeedback(outcome.getDescription());
            form.setComment(outcome.getComment());
        });
    }

    private Optional<ProcessOutcomeResource> getOutcome(Long assessmentId) {
        return getAssessment(assessmentId).getProcessOutcomes().stream().reduce((id1, id2) -> id2).map(id -> processOutcomeService.getById(id));
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplicationForAssessment(Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private ApplicationResource getApplication(Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(AssessmentResource assessment) {
        return processRoleService.getById(assessment.getProcessRole());
    }

    private Long getApplicationIdForProcessRole(Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }
}