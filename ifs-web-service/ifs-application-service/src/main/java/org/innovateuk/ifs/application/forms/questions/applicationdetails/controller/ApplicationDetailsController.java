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
import org.innovateuk.ifs.application.resource.CompanyAge;
import org.innovateuk.ifs.application.resource.CompanyPrimaryFocus;
import org.innovateuk.ifs.application.resource.CompetitionReferralSource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.LocalDatePropertyEditor.convertMinLocalDateToNull;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/application-details")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit application details", securedType = ApplicationDetailsController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class ApplicationDetailsController {

    @Autowired
    private ApplicationDetailsViewModelPopulator applicationDetailsViewModelPopulator;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private ApplicantRestService applicantRestService;
    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping
    public String viewDetails(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationDetailsForm form,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              Model model,
                              @PathVariable long applicationId,
                              @PathVariable long questionId,
                              UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        applicationNavigationPopulator.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), user.hasRole(SUPPORT));
        ApplicantQuestionResource question = applicantRestService.getQuestion(user.getId(), applicationId, questionId);
        ApplicationDetailsViewModel viewModel = applicationDetailsViewModelPopulator.populate(question, competition);
        form.populateForm(viewModel);

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

        return format("redirect:/application/%d", applicationId);
    }

    @PostMapping("/auto-save")
    public @ResponseBody
    JsonNode autoSaveAndReturn(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                               BindingResult bindingResult,
                               Model model,
                               @PathVariable long applicationId,
                               @PathVariable long questionId,
                               UserResource user) {
        saveDetails(form, applicationId);

        return new ObjectMapper().createObjectNode();
    }

    @GetMapping(params = "show-errors")
    public String showErrors(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) ApplicationDetailsForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable long applicationId,
                             @PathVariable long questionId,
                             UserResource user) {
        String view = viewDetails(form, bindingResult, model, applicationId, questionId, user);
        validator.validate(form, bindingResult);
        return view;

    }

    @PostMapping(params = "mark_as_complete")
    public String markAsComplete(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM) @Valid ApplicationDetailsForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        Supplier<String> failureView = () -> viewDetailsPage(form, bindingResult, model, applicationId, questionId, user);
        Supplier<String> successView = () -> format("redirect:/application/%d/form/question/%d/application-details", applicationId, questionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<ValidationMessages> result = saveDetails(form, applicationId);

            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> {
                        ProcessRoleResource role = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
                        questionStatusRestService.markAsComplete(questionId, applicationId, role.getId()).getSuccess().forEach(validationHandler::addAnyErrors);
                        return validationHandler.failNowOrSucceedWith(failureView, successView);
                    });
        });
    }

    private String viewDetailsPage(ApplicationDetailsForm form,
                                   BindingResult bindingResult,
                                   Model model, long applicationId,
                                   long questionId, UserResource user) {
        form.setStartDate(convertMinLocalDateToNull(form.getStartDate()));
        return viewDetails(form, bindingResult, model, applicationId, questionId, user);
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

    private ServiceResult<ValidationMessages> saveDetails(ApplicationDetailsForm form, long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        application.setName(form.getName());
        application.setStartDate(convertMinLocalDateToNull(form.getStartDate()));
        application.setDurationInMonths(form.getDurationInMonths());
        application.setResubmission(form.getResubmission());
        application.setPreviousApplicationNumber(form.getResubmission() == TRUE ? form.getPreviousApplicationNumber() : null);
        application.setPreviousApplicationTitle(form.getResubmission() == TRUE ? form.getPreviousApplicationTitle() : null);
        if (isValidEnum(CompetitionReferralSource.class, form.getCompetitionReferralSource())) {
            application.setCompetitionReferralSource(CompetitionReferralSource.valueOf(form.getCompetitionReferralSource()));
        }
        if (isValidEnum(CompanyAge.class, form.getCompanyAge())) {
            application.setCompanyAge(CompanyAge.valueOf(form.getCompanyAge()));
        }
        if (isValidEnum(CompanyPrimaryFocus.class, form.getCompanyPrimaryFocus())) {
            application.setCompanyPrimaryFocus(CompanyPrimaryFocus.valueOf(form.getCompanyPrimaryFocus()));
        }
        return applicationService.save(application);
    }

}
