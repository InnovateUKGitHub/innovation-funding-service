package org.innovateuk.ifs.project.setupcomplete.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.project.setupcomplete.form.ProjectSetupCompleteForm;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.ProjectSetupCompleteViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static java.lang.String.format;

@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}")
@PreAuthorize("hasAuthority('project_finance')")
@SecuredBySpring(value = "PROJECT_SETUP_COMPLETE", description = "Project finance can view the setup complete page and make changes")
public class ProjectSetupCompleteController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectStateRestService projectStateRestService;

    @Value("${ifs.loan.partb.enabled}")
    private boolean ifsLoanPartBEnabled;

    @GetMapping(value={"/setup-complete", "/loan-setup-complete"})
    public String viewSetupCompletePage(@ModelAttribute(name = "form", binding = false) ProjectSetupCompleteForm form,
                                        @PathVariable long projectId,
                                        @PathVariable long competitionId,
                                        Model model,
                                        UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        model.addAttribute("model", new ProjectSetupCompleteViewModel(project));
        if(ifsLoanPartBEnabled && competition.isLoan()) {
            initProjectStartDate(form, project);
            return "project/loan-setup-complete";
        } else {
            return "project/setup-complete";
        }
    }

    @PostMapping("/setup-complete")
    public String saveProjectState(@ModelAttribute(name = "form") ProjectSetupCompleteForm form,
                                   BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @PathVariable long projectId,
                                   @PathVariable long competitionId,
                                   Model model,
                                   UserResource user) {
        validate(form, bindingResult);
        Supplier<String> failureView = () -> viewSetupCompletePage(form, projectId, competitionId, model, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            if (form.getSuccessful()) {
                validationHandler.addAnyErrors(projectStateRestService.markAsSuccessful(projectId));
            } else {
                validationHandler.addAnyErrors(projectStateRestService.markAsUnsuccessful(projectId));
            }
            return validationHandler.failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%d/project/%d/setup-complete", competitionId, projectId));
        });
    }

    @PostMapping("/loan-setup-complete")
    public String saveLoanProjectState(@ModelAttribute(name = "form") ProjectSetupCompleteForm form,
                                   BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @PathVariable long projectId,
                                   @PathVariable long competitionId,
                                   Model model,
                                   UserResource user) {
        validateLoan(form, bindingResult);
        Supplier<String> failureView = () -> viewSetupCompletePage(form, projectId, competitionId, model, user);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            if (form.getSuccessful()) {

                if(isProjectTargetStartDateChanged(projectId, form.getStartDate())) {
                    validationHandler.addAnyErrors(projectStateRestService.markAsSuccessful(projectId, form.getStartDate()));
                } else{
                    validationHandler.addAnyErrors(projectStateRestService.markAsSuccessful(projectId, null));
                }
            } else {
                validationHandler.addAnyErrors(projectStateRestService.markAsUnsuccessful(projectId));
            }
            return validationHandler.failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%d/project/%d/setup-complete", competitionId, projectId));
        });
    }

    private void initProjectStartDate(ProjectSetupCompleteForm form, ProjectResource project) {
        form.setStartDateYear(ObjectUtils.defaultIfNull(form.getStartDateYear(), project.getTargetStartDate().getYear()));
        form.setStartDateMonth(ObjectUtils.defaultIfNull(form.getStartDateMonth(), project.getTargetStartDate().getMonthValue()));
        form.setStartDateDay(ObjectUtils.defaultIfNull(form.getStartDateDay(), project.getTargetStartDate().getDayOfMonth()));
    }

    private boolean isProjectTargetStartDateChanged(long projectId, LocalDate newTargetStartDate) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        return ChronoUnit.DAYS.between(newTargetStartDate, project.getTargetStartDate()) != 0;
    }

    private void validate(ProjectSetupCompleteForm form, BindingResult bindingResult) {
        if (form.getSuccessful() == null) {
            bindingResult.rejectValue("successful", "validation.field.must.not.be.blank");
        } else if (Boolean.TRUE.equals(form.getSuccessful())) {
            if (!form.isSuccessfulConfirmation()) {
                bindingResult.rejectValue("successfulConfirmation", "validation.field.must.not.be.blank");
            }
        } else if (Boolean.FALSE.equals(form.getSuccessful())) {
            if (!form.isUnsuccessfulConfirmation()) {
                bindingResult.rejectValue("unsuccessfulConfirmation", "validation.field.must.not.be.blank");
            }
        }
    }

    private void validateLoan(ProjectSetupCompleteForm form, BindingResult bindingResult) {
        if (form.getSuccessful() == null) {
            bindingResult.rejectValue("successful", "validation.field.must.not.be.blank");
        } else if (!form.isSuccessfulConfirmation()) {
            bindingResult.rejectValue("successfulConfirmation", "validation.field.must.not.be.blank");
        } else if (Boolean.TRUE.equals(form.getSuccessful())) {
            if (form.getStartDate() == null) {
                bindingResult.rejectValue("startDate", "validation.standard.date.format");
            }
        }
    }
}
