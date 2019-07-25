package org.innovateuk.ifs.application.forms.questions.applicationdetails.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.form.ApplicationDetailsForm;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.populator.ApplicationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_MODEL;
import static org.innovateuk.ifs.controller.LocalDatePropertyEditor.convertMinLocalDateToNull;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/application-details")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit application details", securedType = ApplicationDetailsController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead', 'assessor', 'monitoring_officer')")
public class ApplicationDetailsController {

    private ApplicationDetailsViewModelPopulator applicationDetailsViewModelPopulator;
    private QuestionStatusRestService questionStatusRestService;
    private UserRestService userRestService;
    private ApplicantRestService applicantRestService;
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    private ApplicationService applicationService;

    public ApplicationDetailsController(ApplicationDetailsViewModelPopulator applicationDetailsViewModelPopulator,
                                        QuestionStatusRestService questionStatusRestService,
                                        UserRestService userRestService,
                                        ApplicantRestService applicantRestService,
                                        ApplicationNavigationPopulator applicationNavigationPopulator,
                                        ApplicationService applicationService) {
        this.applicationDetailsViewModelPopulator = applicationDetailsViewModelPopulator;
        this.questionStatusRestService = questionStatusRestService;
        this.userRestService = userRestService;
        this.applicantRestService = applicantRestService;
        this.applicationNavigationPopulator = applicationNavigationPopulator;
        this.applicationService = applicationService;
    }

    @GetMapping
    public String viewDetails(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationDetailsForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              Model model,
                              @PathVariable long applicationId,
                              @PathVariable long questionId,
                              UserResource user) {
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), user.hasRole(SUPPORT));
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        ApplicationDetailsViewModel viewModel = applicationDetailsViewModelPopulator.populate(question);
        form.populateForm(viewModel);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
        model.addAttribute(MODEL_ATTRIBUTE_MODEL, viewModel);

        return "application/questions/application-details";
    }

    @PostMapping
    public String saveAndReturn(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                                BindingResult bindingResult,
                                Model model,
                                @PathVariable long applicationId,
                                @PathVariable long questionId,
                                UserResource user) {
        saveDetails(form, applicationId);

        return String.format("redirect:/application/%d", applicationId);
    }

    @PostMapping("/auto-save")
    public @ResponseBody JsonNode autoSaveAndReturn(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                               BindingResult bindingResult,
                               Model model,
                               @PathVariable long applicationId,
                               @PathVariable long questionId,
                               UserResource user) {
        saveDetails(form, applicationId);

        return new ObjectMapper().createObjectNode();
    }

    @GetMapping(params = "mark_as_complete")
    public String markAsCompleteAsGetRequest(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) @Valid ApplicationDetailsForm form,
                                             BindingResult bindingResult,
                                             Model model,
                                             @PathVariable long applicationId,
                                             @PathVariable long questionId,
                                             UserResource user) {
        return markAsComplete(form, bindingResult, model, applicationId, questionId, user);
    }

    @PostMapping(params = "mark_as_complete")
    public String markAsComplete(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) @Valid ApplicationDetailsForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        if (bindingResult.hasErrors()) {
            form.setStartDate(convertMinLocalDateToNull(form.getStartDate()));
            return viewDetails(form, bindingResult, model, applicationId, questionId, user);
        }
        saveDetails(form, applicationId);
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsComplete(questionId, applicationId, role.getId()).getSuccess();

        return String.format("redirect:/application/%d/form/question/%d/application-details", applicationId, questionId);
    }

    @PostMapping(params = "change_innovation_area")
    public String changeInnovationArea(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        saveDetails(form, applicationId);

        return String.format("redirect:/application/%d/form/question/%d/innovation-area", applicationId, questionId);
    }

    @PostMapping(params = "mark_as_incomplete")
    public String edit(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                       BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user) {
        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId()).getSuccess();

        return viewDetails(form, bindingResult, model, applicationId, questionId, user);
    }

    private void saveDetails(ApplicationDetailsForm form, long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        application.setName(form.getName());
        application.setStartDate(convertMinLocalDateToNull(form.getStartDate()));
        application.setDurationInMonths(form.getDurationInMonths());
        application.setResubmission(form.getResubmission());
        application.setPreviousApplicationNumber(form.getResubmission() == TRUE ? form.getPreviousApplicationNumber() : null);
        application.setPreviousApplicationTitle(form.getResubmission() == TRUE ? form.getPreviousApplicationTitle() : null);
        applicationService.save(application);
    }

}
