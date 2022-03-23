package org.innovateuk.ifs.application.forms.questions.heukar.controller;

import org.innovateuk.ifs.horizon.service.ApplicationHeukarLocationRestService;
import org.innovateuk.ifs.application.forms.questions.heukar.HeukarProjectLocationCookieService;
import org.innovateuk.ifs.application.forms.questions.heukar.form.HeukarProjectLocationForm;
import org.innovateuk.ifs.application.forms.questions.heukar.model.HeukarProjectLocationSelectionData;
import org.innovateuk.ifs.application.forms.questions.heukar.model.HeukarProjectLocationViewModel;
import org.innovateuk.ifs.application.forms.questions.heukar.populator.HeukarProjectLocationPopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MODEL_ATTRIBUTE_FORM;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.defaultConverters;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.newFieldError;
import static org.innovateuk.ifs.heukar.resource.HeukarLocation.*;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/heukar-project-location")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit project location", securedType = HeukarProjectLocationController.class)
@PreAuthorize("hasAnyAuthority('applicant')")
public class HeukarProjectLocationController {

    private static final String TEMPLATE_PATH = "application/questions/heukar-project-locations";

    @Autowired
    private HeukarProjectLocationCookieService cookieService;

    @Autowired
    private HeukarProjectLocationPopulator heukarProjectLocationPopulator;

    @Autowired
    private ApplicationHeukarLocationRestService heukarLocationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    private List<HeukarLocation> parentsForWorkflow = new ArrayList<>();

    @GetMapping
    public String viewParentProjectLocations(@ModelAttribute(value = "form", binding = true) HeukarProjectLocationForm form,
                                             BindingResult bindingResult,
                                             Model model,
                                             @PathVariable long applicationId,
                                             @PathVariable long questionId,
                                             UserResource user,
                                             HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestParam(defaultValue = "false") boolean readOnly) {
        resetWorkflow();

        Optional<HeukarProjectLocationSelectionData> cookieSelectionData = cookieService.getProjectLocationSelectionData(request);
        if (cookieSelectionData.isPresent()) {
            if (cookieSelectionData.get().getApplicationId() != applicationId) {
                cookieService.deleteProjectLocationSelectionData(response);
            }
        }

        List<HeukarLocation> existingSelections = cookieService.getProjectLocationSelectionData(request)
                .map(HeukarProjectLocationSelectionData::getParentSelections)
                .orElse(emptyList());

        form.setAllOptions(newArrayList(parentLocations));
        form.setSelected(existingSelections);
        model.addAttribute("form", form);

        // TODO move to the populator
        HeukarProjectLocationViewModel viewModel = heukarProjectLocationPopulator.populate(applicationId, questionId, user.getId(), null, emptyMap());
        if (viewModel.isComplete() || readOnly) {
            viewModel = heukarProjectLocationPopulator.populate(applicationId, questionId, user.getId(), null, getReadOnlyMap(applicationId, request));
        }

        model.addAttribute("model", viewModel);

        return TEMPLATE_PATH;
    }

    private void resetWorkflow() {
        parentsForWorkflow = new ArrayList<>();
    }

    @PostMapping
    public String submitParentProjectLocations(@Valid @ModelAttribute(value = "form") HeukarProjectLocationForm form,
                                               BindingResult bindingResult,
                                               Model model,
                                               @PathVariable long applicationId,
                                               @PathVariable long questionId,
                                               UserResource user,
                                               HeukarLocation parent,
                                               ValidationHandler validationHandler,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {

        List<HeukarLocation> selectedLocations = form.getSelected();
        Supplier<String> failureView = () -> viewParentProjectLocations(form, bindingResult, model, applicationId, questionId, user, request, response, false);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            HeukarProjectLocationSelectionData heukarProjectLocationSelectionData = new HeukarProjectLocationSelectionData(applicationId);
            heukarProjectLocationSelectionData.setParentSelections(form.getSelected());
            cookieService.saveProjectLocationSelectionData(heukarProjectLocationSelectionData, response);

            if (this.parentsForWorkflow.isEmpty()) {
                setParentWorkflow(selectedLocations);
            }

            return completeWorkflowStage(applicationId, questionId, null);
        });
    }

    private void setParentWorkflow(List<HeukarLocation> selectedParentLocations) {
        this.parentsForWorkflow = selectedParentLocations
                .stream()
                .filter(location -> !findChildrenOf(location).isEmpty())
                .collect(Collectors.toList());
    }


    @GetMapping("/england")
    public String englandLocations(@ModelAttribute(value = "form", binding = true) HeukarProjectLocationForm form,
                                   BindingResult bindingResult,
                                   Model model,
                                   @PathVariable long applicationId,
                                   @PathVariable long questionId,
                                   UserResource user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        List<HeukarLocation> existingSelections = cookieService.getProjectLocationSelectionData(request)
                .map(HeukarProjectLocationSelectionData::getEnglandSelections)
                .orElse(emptyList());

        form.setAllOptions(findChildrenOf(ENGLAND));
        form.setSelected(existingSelections);

        model.addAttribute("form", form);
        model.addAttribute("model", heukarProjectLocationPopulator.populate(applicationId, questionId, user.getId(), ENGLAND, emptyMap()));

        return TEMPLATE_PATH;
    }

    @PostMapping("/england")
    public String submitEnglandSubLocations(@ModelAttribute(value = "form") @Valid HeukarProjectLocationForm form,
                                            BindingResult bindingResult,
                                            Model model,
                                            @PathVariable long applicationId,
                                            @PathVariable long questionId,
                                            UserResource user,
                                            ValidationHandler validationHandler,
                                            HttpServletRequest request,
                                            HttpServletResponse response
    ) {
        Supplier<String> failureView = () -> englandLocations(form, bindingResult, model, applicationId, questionId, user, request, response);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            HeukarProjectLocationSelectionData heukarProjectLocationSelectionData = cookieService.getProjectLocationSelectionData(request).get();
            heukarProjectLocationSelectionData.setEnglandSelections(form.getSelected());
            cookieService.saveProjectLocationSelectionData(heukarProjectLocationSelectionData, response);

            return completeWorkflowStage(applicationId, questionId, ENGLAND);
        });
    }

    @GetMapping("/overseas")
    public String overseasSubLocations(@ModelAttribute(value = "form", binding = true) HeukarProjectLocationForm form,
                                       BindingResult bindingResult,
                                       Model model,
                                       @PathVariable long applicationId,
                                       @PathVariable long questionId,
                                       UserResource user,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        List<HeukarLocation> existingSelections = cookieService.getProjectLocationSelectionData(request)
                .map(HeukarProjectLocationSelectionData::getOverseasSelections)
                .orElse(emptyList());

        form.setAllOptions(findChildrenOf(BRITISH_OVERSEAS_TERRITORY));
        form.setSelected(existingSelections);

        model.addAttribute("form", form);
        model.addAttribute("model", heukarProjectLocationPopulator.populate(applicationId, questionId, user.getId(), BRITISH_OVERSEAS_TERRITORY, emptyMap()));
        return TEMPLATE_PATH;
    }

    @PostMapping("/overseas")
    public String submitOverseasSubLocations(@ModelAttribute(value = "form") @Valid HeukarProjectLocationForm form,
                                             BindingResult bindingResult,
                                             Model model,
                                             @PathVariable long applicationId,
                                             @PathVariable long questionId,
                                             UserResource user,
                                             ValidationHandler validationHandler,
                                             HttpServletRequest request,
                                             HttpServletResponse response
    ) {
        Supplier<String> failureView = () -> overseasSubLocations(form, bindingResult, model, applicationId, questionId, user, request, response);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            HeukarProjectLocationSelectionData heukarProjectLocationSelectionData = cookieService.getProjectLocationSelectionData(request).get();
            heukarProjectLocationSelectionData.setOverseasSelections(form.getSelected());
            cookieService.saveProjectLocationSelectionData(heukarProjectLocationSelectionData, response);

            return completeWorkflowStage(applicationId, questionId, BRITISH_OVERSEAS_TERRITORY);
        });
    }

    @GetMapping("/crown_dependency")
    public String crownDependencySubLocations(@ModelAttribute(value = "form", binding = true) HeukarProjectLocationForm form,
                                              BindingResult bindingResult,
                                              Model model,
                                              @PathVariable long applicationId,
                                              @PathVariable long questionId,
                                              UserResource user,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {

        List<HeukarLocation> existingSelections =
                cookieService.getProjectLocationSelectionData(request)
                        .map(HeukarProjectLocationSelectionData::getCrownDependencySelections)
                        .orElse(emptyList());

        form.setAllOptions(findChildrenOf(CROWN_DEPENDENCY));
        form.setSelected(existingSelections);

        model.addAttribute("form", form);
        model.addAttribute("model", heukarProjectLocationPopulator.populate(applicationId, questionId, user.getId(), CROWN_DEPENDENCY, emptyMap()));
        return TEMPLATE_PATH;
    }

    @PostMapping("/crown_dependency")
    public String submitCrownDependencySubLocations(@ModelAttribute(value = "form") @Valid HeukarProjectLocationForm form,
                                                    BindingResult bindingResult,
                                                    Model model,
                                                    @PathVariable long applicationId,
                                                    @PathVariable long questionId,
                                                    UserResource user,
                                                    ValidationHandler validationHandler,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response
    ) {
        Supplier<String> failureView = () -> crownDependencySubLocations(form, bindingResult, model, applicationId, questionId, user, request, response);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            HeukarProjectLocationSelectionData heukarProjectLocationSelectionData = cookieService.getProjectLocationSelectionData(request).get();
            heukarProjectLocationSelectionData.setCrownDependencySelections(form.getSelected());
            cookieService.saveProjectLocationSelectionData(heukarProjectLocationSelectionData, response);
            return completeWorkflowStage(applicationId, questionId, CROWN_DEPENDENCY);
        });
    }


    private String completeWorkflowStage(long applicationId, long questionId, HeukarLocation location) {
        Optional.ofNullable(location).ifPresent(loc -> {
            removeCompletedFromWorkflow(location);
        });
        if (this.parentsForWorkflow.isEmpty()) {
            return redirectToProjectLocation(applicationId, questionId, true);
        }
        HeukarLocation nextParentLocation = this.parentsForWorkflow.get(0);
        switch (nextParentLocation) {
            case ENGLAND:
                return redirectToEnglandSubLocations(applicationId, questionId);
            case BRITISH_OVERSEAS_TERRITORY:
                return redirectToOverseasSubLocations(applicationId, questionId);
            case CROWN_DEPENDENCY:
                return redirectToCrownDependencySubLocations(applicationId, questionId);
            default:
                return redirectToProjectLocation(applicationId, questionId);
        }
    }

    private void removeCompletedFromWorkflow(HeukarLocation selectedLocation) {
        this.parentsForWorkflow.remove(selectedLocation);
    }

    private Map<String, List<HeukarLocation>> getReadOnlyMap(long applicationId, HttpServletRequest request) {

        Optional<HeukarProjectLocationSelectionData> projectLocationSelectionData = cookieService.getProjectLocationSelectionData(request);
        List<HeukarLocation> savedSelections = projectLocationSelectionData.isPresent() && !projectLocationSelectionData.get().getAllSelections().isEmpty() ?
                projectLocationSelectionData.get().getAllSelections() :
                heukarLocationRestService.findAllWithApplicationId(applicationId).getSuccess()
                        .stream()
                        .map(e -> valueOf(e.getLocation()))
                        .collect(toList());

        return savedSelections.stream()
                .filter(e -> !e.equals(BRITISH_OVERSEAS_TERRITORY))
                .filter(e -> !e.equals(CROWN_DEPENDENCY))
                .collect(Collectors.groupingBy(HeukarLocation::getRegion));
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(value = "form") HeukarProjectLocationForm form,
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
                mapProjectLocationCompletionError(),
                defaultConverters()));
        return validationHandler.failNowOrSucceedWith(() -> {
                    questionStatusRestService.markAsInComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();
                    return viewParentProjectLocations(form, bindingResult, model, applicationId, questionId, user, request, response, false);
                },
                () -> {
                    List<HeukarLocation> allSelected = cookieService.getProjectLocationSelectionData(request).get().getAllSelections();
                    heukarLocationRestService.updateLocationsForApplication(allSelected, applicationId);

                    return redirectToProjectLocation(applicationId, questionId, true);
                });
    }

    @PostMapping(params = "edit")
    public String edit(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) HeukarProjectLocationForm form,
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

        return viewParentProjectLocations(form, bindingResult, model, applicationId, questionId, user, request, response, false);
    }

    private ErrorToObjectErrorConverter mapProjectLocationCompletionError() {
        return e -> {
            if ("validation.projectlocation.required".equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, "organisation." + e.getArguments().get(1), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    private String redirectToProjectLocation(long applicationId, long questionId){
        return redirectToProjectLocation(applicationId, questionId, false);
    }

    private String redirectToProjectLocation(long applicationId, long questionId, boolean readOnly) {
        String baseUrl = String.format("redirect:/application/%d/form/question/%d/heukar-project-location", applicationId, questionId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("readOnly", readOnly);
        return builder.toUriString();
    }

    private long processRoleId(long userId, long applicationId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }

    private String redirectToEnglandSubLocations(long applicationId, long questionId) {
        return constructRedirect(applicationId, questionId, "england");
    }

    private String redirectToOverseasSubLocations(long applicationId, long questionId) {
        return constructRedirect(applicationId, questionId, "overseas");
    }

    private String redirectToCrownDependencySubLocations(long applicationId, long questionId) {
        return constructRedirect(applicationId, questionId, "crown_dependency");
    }

    private String constructRedirect(long applicationId, long questionId, String appendage) {
        return "redirect:" + APPLICATION_BASE_URL + applicationId + "/form/question/" + questionId + "/heukar-project-location/" + appendage;
    }
}
