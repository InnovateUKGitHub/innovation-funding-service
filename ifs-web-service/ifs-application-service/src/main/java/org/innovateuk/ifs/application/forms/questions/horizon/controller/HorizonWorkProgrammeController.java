package org.innovateuk.ifs.application.forms.questions.horizon.controller;

import org.innovateuk.ifs.application.forms.questions.horizon.HorizonWorkProgrammeCookieService;
import org.innovateuk.ifs.application.forms.questions.horizon.form.HorizonWorkProgrammeForm;
import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeSelectionData;
import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeViewModel;
import org.innovateuk.ifs.application.forms.questions.horizon.populator.HorizonWorkProgrammePopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.service.HorizonWorkProgrammeRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.newFieldError;
import static org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme.workProgrammes;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/horizon-work-programme")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit work programme.", securedType = HorizonWorkProgrammeController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class HorizonWorkProgrammeController {

    private static final String TEMPLATE_PATH = "application/questions/horizon-work-programmes";

    @Autowired
    private HorizonWorkProgrammePopulator horizonWorkProgrammePopulator;

    @Autowired
    private HorizonWorkProgrammeRestService horizonWorkProgrammeRestService;

    @Autowired
    private HorizonWorkProgrammeCookieService cookieService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    private List<HorizonWorkProgramme> workflow = new ArrayList<>();

    @GetMapping
    public String viewWorkProgramme(@ModelAttribute(value = "form") HorizonWorkProgrammeForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    @PathVariable long applicationId,
                                    @PathVariable long questionId,
                                    UserResource user,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestParam(defaultValue = "false") boolean readOnly) {

        resetWorkflow();

        Optional<HorizonWorkProgrammeSelectionData> cookieSelectionData = getHorizonWorkProgrammeSelectionData(request);

        if (cookieSelectionData.isPresent()) {
            if (cookieSelectionData.get().getApplicationId() != applicationId) {
                cookieService.deleteWorkProgrammeSelectionData(response);
            }
        }

        HorizonWorkProgramme existingSelection = cookieSelectionData
                .map(HorizonWorkProgrammeSelectionData::getWorkProgramme)
                .orElse(null);

        form.setAllOptions(newArrayList(workProgrammes));
        form.setSelected(existingSelection);
        model.addAttribute("form", form);

        HorizonWorkProgrammeViewModel viewModel = getPopulate(applicationId, questionId, user, false, emptyMap());

        getReadOnlyMapIfApplicable(applicationId, request, readOnly, viewModel);

        model.addAttribute("form", form);
        model.addAttribute("model", viewModel);

        return TEMPLATE_PATH;
    }

    @PostMapping
    public String submitWorkProgramme(@Valid @ModelAttribute(value = "form") HorizonWorkProgrammeForm form,
                                      BindingResult bindingResult,
                                      Model model,
                                      @PathVariable long applicationId,
                                      @PathVariable long questionId,
                                      UserResource user,
                                      ValidationHandler validationHandler,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        List<HorizonWorkProgramme> selectWorkProgramme = Collections.singletonList(form.getSelected());
        Supplier<String> failureView = () -> viewWorkProgramme(form, bindingResult, model, applicationId, questionId, user, request, response, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            HorizonWorkProgrammeSelectionData horizonWorkProgrammeSelectionData = new HorizonWorkProgrammeSelectionData(applicationId);
            horizonWorkProgrammeSelectionData.setWorkProgramme(form.getSelected());
            saveSelectionToCookie(horizonWorkProgrammeSelectionData, response);

            if (this.workflow.isEmpty()) {
                setWorkflow(selectWorkProgramme);
            }

            return redirectToCallId(applicationId, questionId);
        });
    }

    @GetMapping("/call-id")
    public String viewCallIds(@ModelAttribute(value = "form") HorizonWorkProgrammeForm form,
                              BindingResult bindingResult,
                              Model model,
                              @PathVariable long applicationId,
                              @PathVariable long questionId,
                              UserResource user,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        Optional<HorizonWorkProgrammeSelectionData> cookieSelectionData = getHorizonWorkProgrammeSelectionData(request);

        HorizonWorkProgramme existingSelection = cookieSelectionData
                .map(HorizonWorkProgrammeSelectionData::getCallId)
                .orElse(null);

        form.setAllOptions(getChildrenOf(cookieSelectionData));
        form.setSelected(existingSelection);

        model.addAttribute("form", form);
        model.addAttribute("model",
                getPopulate(applicationId, questionId, user, true, emptyMap()));

        return TEMPLATE_PATH;
    }

    @PostMapping("/call-id")
    public String submitCallId(@ModelAttribute(value = "form") @Valid HorizonWorkProgrammeForm form,
                               BindingResult bindingResult,
                               Model model,
                               @PathVariable long applicationId,
                               @PathVariable long questionId,
                               UserResource user,
                               ValidationHandler validationHandler,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Supplier<String> failureView = () -> viewCallIds(form, bindingResult, model, applicationId, questionId, user, request, response);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            HorizonWorkProgrammeSelectionData horizonWorkProgrammeSelectionData = cookieService.getHorizonWorkProgrammeSelectionData(request).get();
            horizonWorkProgrammeSelectionData.setCallId(form.getSelected());
            saveSelectionToCookie(horizonWorkProgrammeSelectionData, response);

            return completeWorkflow(applicationId, questionId);
        });
    }

    private void getReadOnlyMapIfApplicable(long applicationId, HttpServletRequest request, boolean readOnly, HorizonWorkProgrammeViewModel viewModel) {
        if (viewModel.isComplete() || readOnly) {
            viewModel.setReadOnlyMap(getReadOnlyMapIfApplicable(applicationId, request));

            if (!viewModel.getReadOnlyMap().isEmpty()) {
                viewModel.setReadOnly(true);
            }
        }
    }

    private void saveSelectionToCookie(HorizonWorkProgrammeSelectionData horizonWorkProgrammeSelectionData, HttpServletResponse response) {
        cookieService.saveWorkProgrammeSelectionData(horizonWorkProgrammeSelectionData, response);
    }

    private void setWorkflow(List<HorizonWorkProgramme> selectedWorkProgramme) {
        this.workflow = selectedWorkProgramme
                .stream()
                .filter(programme -> !HorizonWorkProgramme.findChildrenOf(programme).isEmpty())
                .collect(Collectors.toList());
    }

    private long processRoleId(long userId, long applicationId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }

    private String redirectToWorkProgramme(long applicationId, long questionId, boolean readOnly) {
        String baseUrl = String.format("redirect:/application/%d/form/question/%d/horizon-work-programme", applicationId, questionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("readOnly", readOnly);

        return builder.toUriString();
    }

    private Map<String, List<HorizonWorkProgramme>> getReadOnlyMapIfApplicable(long applicationId, HttpServletRequest request) {

        Optional<HorizonWorkProgrammeSelectionData> workProgrammeSelectionData = cookieService.getHorizonWorkProgrammeSelectionData(request);
        List<HorizonWorkProgramme> savedSelections = (workProgrammeSelectionData.isPresent() && !workProgrammeSelectionData.get().getAllSelections().isEmpty()) ?
                workProgrammeSelectionData.get().getAllSelections() :
                horizonWorkProgrammeRestService.findSelected(applicationId).getSuccess()
                        .stream()
                        .map(e -> e.getWorkProgramme())
                        .collect(toList());

        return savedSelections
                .stream()
                .sorted(nullsFirst(comparing(HorizonWorkProgramme::getWorkProgramme, nullsFirst(naturalOrder()))))
                .collect(Collectors.groupingBy(HorizonWorkProgramme::getDisplay));
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(value = "form") HorizonWorkProgrammeForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user,
                                 HttpServletRequest request,
                                 HttpServletResponse response
    ) {
        List<ValidationMessages> validationMessages =
                questionStatusRestService.markAsComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();

        validationMessages.forEach(messages -> validationHandler.addAnyErrors(messages,
                mapWorkProgrammeCompletionError(),
                defaultConverters()));
        return validationHandler.failNowOrSucceedWith(() -> {
                    questionStatusRestService.markAsInComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();
                    return viewWorkProgramme(form, bindingResult, model, applicationId, questionId, user, request, response, false);
                },
                () -> {
                    List<HorizonWorkProgramme> allSelected = cookieService.getHorizonWorkProgrammeSelectionData(request).get().getAllSelections();
                    horizonWorkProgrammeRestService.updateWorkProgrammeForApplication(allSelected, applicationId);

                    return redirectToWorkProgramme(applicationId, questionId, true);
                });
    }

    @PostMapping(params = "edit")
    public String edit(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) HorizonWorkProgrammeForm form,
                       BindingResult bindingResult,
                       Model model,
                       @PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user,
                       HttpServletRequest request,
                       HttpServletResponse response
    ) {
        ProcessRoleResource role = processRoleRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId()).getSuccess();

        return viewWorkProgramme(form, bindingResult, model, applicationId, questionId, user, request, response, false);
    }

    private HorizonWorkProgrammeViewModel getPopulate(long applicationId, long questionId, UserResource user, boolean isCallId, Map<String, List<HorizonWorkProgramme>> readOnlyMap) {
        return horizonWorkProgrammePopulator.populate(applicationId, questionId, user, isCallId, readOnlyMap);
    }

    private List<HorizonWorkProgramme> getChildrenOf(Optional<HorizonWorkProgrammeSelectionData> cookieSelectionData) {
        return HorizonWorkProgramme.findChildrenOf(cookieSelectionData.get().getWorkProgramme());
    }

    private Optional<HorizonWorkProgrammeSelectionData> getHorizonWorkProgrammeSelectionData(HttpServletRequest request) {
        return cookieService.getHorizonWorkProgrammeSelectionData(request);
    }

    private String redirectToCallId(long applicationId, long questionId) {
        return "redirect:" + APPLICATION_BASE_URL + applicationId + "/form/question/" + questionId + "/horizon-work-programme" + "/call-id";
    }

    private String completeWorkflow(long applicationId, long questionId) {
        return redirectToWorkProgramme(applicationId, questionId, true);
    }

    private ErrorToObjectErrorConverter mapWorkProgrammeCompletionError() {
        return e -> {
            if ("validation.horizon.programme.required".equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, "organisation." + e.getArguments().get(1), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    private void resetWorkflow() {
        workflow = new ArrayList<>();
    }
}