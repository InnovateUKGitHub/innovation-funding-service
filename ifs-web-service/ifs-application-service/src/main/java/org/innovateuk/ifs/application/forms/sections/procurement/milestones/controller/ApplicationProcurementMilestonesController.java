package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ApplicationProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.ApplicationProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator.ProcurementMilestoneFormValidator;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "UPDATE_PROCUREMENT_MILESTONE", description = "Applicants can update procurement milestones.")
public class ApplicationProcurementMilestonesController {
    private static final String VIEW = "application/sections/procurement-milestones/application-procurement-milestones";
    private static final Log LOG = LogFactory.getLog(ApplicationProcurementMilestonesController.class);

    @Autowired
    private ProcurementMilestoneFormPopulator formPopulator;

    @Autowired
    private ApplicationProcurementMilestoneRestService restService;

    @Autowired
    private ApplicationProcurementMilestoneFormSaver saver;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private ApplicationProcurementMilestoneViewModelPopulator viewModelPopulator;

    @Autowired
    private ProcurementMilestoneFormValidator validator;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('applicant', 'support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder', 'external_finance', 'knowledge_transfer_adviser', 'supporter', 'assessor')")
    @SecuredBySpring(value = "VIEW_PROCUREMENT_MILESTONE", description = "Everyone view the milestone page, if they have permissions defined in data layer.")
    public String viewMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 UserResource user,
                                 Model model) {
        ProcurementMilestonesForm form = formPopulator.populate(restService.getByApplicationIdAndOrganisationId(applicationId, organisationId).getSuccess());
        model.addAttribute("form", form);
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    @PostMapping
    public String saveMilestones(@PathVariable long applicationId,
                                 @PathVariable long organisationId,
                                 @PathVariable long sectionId,
                                 @ModelAttribute("form") ProcurementMilestonesForm form) {
        try {
            saver.save(form, applicationId, organisationId).getSuccess();
        } catch (Exception e) {
            LOG.error(e);
        }
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "complete")
    public String complete(Model model,
                           UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable long organisationId,
                           @PathVariable long sectionId,
                           @Valid @ModelAttribute("form") ProcurementMilestonesForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler) {
        validator.validate(form, applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess(), validationHandler);
        Supplier<String> successView = () -> redirectToYourFinances(applicationId);
        Supplier<String> failureView = () -> viewMilestones(model, form, user, applicationId, organisationId, sectionId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(saver.save(form, applicationId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                validationHandler.addAnyErrors(
                        sectionStatusRestService.markAsComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess());
                return validationHandler.failNowOrSucceedWith(failureView, successView);
            });
        });
    }

    @PostMapping(params = "edit")
    public String edit(Model model,
                       UserResource user,
                       @PathVariable long applicationId,
                       @PathVariable long organisationId,
                       @PathVariable long sectionId) {
        sectionStatusRestService.markAsInComplete(sectionId, applicationId, getProcessRoleId(applicationId, user.getId())).getSuccess();
        return String.format("redirect:/application/%d/form/procurement-milestones/organisation/%d/section/%d", applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "remove_row")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") ProcurementMilestonesForm form,
                                BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                @RequestParam("remove_row") String removeId) {
        validationHandler.addAnyErrors(saver.removeRowFromForm(form, removeId));
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    @PostMapping(params = "add_row")
    public String addRowPost(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long organisationId,
                             @PathVariable long sectionId,
                             @ModelAttribute("form") ProcurementMilestonesForm form) {
        saver.addRowForm(form);
        return viewMilestones(model, form, user, applicationId, organisationId, sectionId);
    }

    @PostMapping("auto-save")
    public @ResponseBody
    JsonNode ajaxAutoSave(UserResource user,
                          @PathVariable long applicationId,
                          @PathVariable long organisationId,
                          @RequestParam String field,
                          @RequestParam String value) {
        Optional<Long> fieldId = saver.autoSave(field, value, applicationId, organisationId);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        fieldId.ifPresent(id -> node.put("fieldId", id));
        return node;
    }

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable String rowId) {
        saver.removeRow(rowId);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping("add-row")
    public String ajaxAddRow(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable long organisationId,
                             @PathVariable long sectionId) {
        ProcurementMilestonesForm form = new ProcurementMilestonesForm();
        saver.addRowForm(form);
        Map.Entry<String, ProcurementMilestoneForm> entry = form.getMilestones().entrySet().stream().findFirst().get();

        System.out.println("err");
        model.addAttribute("form", form);
        model.addAttribute("model", viewModelPopulator.populate(user, applicationId, organisationId, sectionId));
        model.addAttribute("id", entry.getKey());
        model.addAttribute("row", entry.getValue());
        return "application/procurement-milestones :: ajax-milestone-row";
    }

    private String viewMilestones(Model model, ProcurementMilestonesForm form, UserResource user, long applicationId, long organisationId, long sectionId) {
        model.addAttribute("model", viewModelPopulator.populate(user, applicationId, organisationId, sectionId));
        form.setMilestones(reorderMilestones(form.getMilestones()));
        return VIEW;
    }

    private Map<String, ProcurementMilestoneForm> reorderMilestones(Map<String, ProcurementMilestoneForm> map) {
        return map.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getMonth(), Comparator.nullsLast(Integer::compareTo)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private long getProcessRoleId(long applicationId, long userId) {
        return getProcessRole(applicationId, userId).getId();
    }

    private ProcessRoleResource getProcessRole(long applicationId, long userId) {
        return processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
    }

    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }
}
