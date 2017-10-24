package org.innovateuk.ifs.application.forms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.exception.AutoSaveElementException;
import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntries;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntry;

/**
 * This controller will handle all Ajax requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationAjaxController {

    private static final Log LOG = LogFactory.getLog(ApplicationAjaxController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder, WebRequest webRequest) {
        dataBinder.registerCustomEditor(String.class, new StringMultipartFileEditor());
    }

    /**
     * This method is for supporting ajax saving from the application form.
     */
    @PostMapping("/{competitionId}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("formInputId") String inputIdentifier,
                                    @RequestParam("value") String value,
                                    @PathVariable(APPLICATION_ID) Long applicationId,
                                    @PathVariable("competitionId") Long competitionId,
                                    UserResource user,
                                    HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        Long fieldId = null;
        try {
            String fieldName = request.getParameter("fieldName");
            LOG.info(String.format("saveFormElement: %s / %s", fieldName, value));

            StoreFieldResult storeFieldResult = storeField(applicationId, user.getId(), competitionId, fieldName, inputIdentifier, value);

            fieldId = storeFieldResult.getFieldId();

            return createJsonObjectNode(true, fieldId);

        } catch (Exception e) {
            AutoSaveElementException ex = new AutoSaveElementException(inputIdentifier, value, applicationId, e);
            handleAutoSaveException(errors, e, ex);
            return createJsonObjectNode(false, fieldId);
        }
    }

    private void handleAutoSaveException(List<String> errors, Exception e, AutoSaveElementException ex) {
        List<Object> args = new ArrayList<>();
        args.add(ex.getErrorMessage());
        if (e.getClass().equals(IntegerNumberFormatException.class) || e.getClass().equals(BigDecimalNumberFormatException.class)) {
            errors.add(lookupErrorMessageResourceBundleEntry(messageSource, e.getMessage(), args));
        } else {
            LOG.error("Got an exception on autosave : " + e.getMessage());
            errors.add(ex.getErrorMessage());
        }
    }

    private StoreFieldResult storeField(Long applicationId, Long userId, Long competitionId, String fieldName, String inputIdentifier, String value) throws NumberFormatException {
        Long organisationType = organisationService.getOrganisationType(userId, applicationId);

        if (fieldName.startsWith("application.")) {

            // this does not need id
            List<String> errors = this.saveApplicationDetails(applicationId, fieldName, value);
            return new StoreFieldResult(errors);
        } else if (inputIdentifier.startsWith("financePosition-") || fieldName.startsWith("financePosition-")) {
            financeHandler.getFinanceFormHandler(organisationType).updateFinancePosition(userId, applicationId, fieldName, value, competitionId);
            return new StoreFieldResult();
        } else if (inputIdentifier.startsWith("formInput[cost-") || fieldName.startsWith("cost-")) {
            ValidationMessages validationMessages = financeHandler.getFinanceFormHandler(organisationType).storeCost(userId, applicationId, fieldName, value, competitionId);

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no errors");
                if (validationMessages == null) {
                    return new StoreFieldResult();
                } else {
                    return new StoreFieldResult(validationMessages.getObjectId());
                }
            } else {
                String[] fieldNameParts = fieldName.split("-");
                // fieldname = other_costs-description-34-219
                List<String> errors = validationMessages.getErrors()
                        .stream()
                        .peek(e -> LOG.debug(String.format("Compare: %s => %s ", fieldName.toLowerCase(), e.getFieldName().toLowerCase())))
                        .filter(e -> fieldNameParts[1].toLowerCase().contains(e.getFieldName().toLowerCase())) // filter out the messages that are related to other fields.
                        .map(this::lookupErrorMessage)
                        .collect(toList());
                return new StoreFieldResult(validationMessages.getObjectId(), errors);
            }
        } else {
            Long formInputId = Long.valueOf(inputIdentifier);
            ValidationMessages saveErrors = formInputResponseRestService.saveQuestionResponse(userId, applicationId,
                    formInputId, value, false).getSuccessObjectOrThrowException();
            List<String> lookedUpErrorMessages = lookupErrorMessageResourceBundleEntries(messageSource, saveErrors);
            return new StoreFieldResult(lookedUpErrorMessages);
        }
    }

    private String lookupErrorMessage(Error e) {
        return lookupErrorMessageResourceBundleEntry(messageSource, e);
    }

    private ObjectNode createJsonObjectNode(boolean success, Long fieldId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        if (fieldId != null) {
            node.set("fieldId", new LongNode(fieldId));
        }
        return node;
    }

    private List<String> saveApplicationDetails(Long applicationId, String fieldName, String value) throws NumberFormatException {
        List<String> errors = new ArrayList<>();
        ApplicationResource application = applicationService.getById(applicationId);

        if ("application.name".equals(fieldName)) {
            String trimmedValue = value.trim();
            if (StringUtils.isEmpty(trimmedValue)) {
                errors.add("Please enter the full title of the project");
            } else {

                application.setName(trimmedValue);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith("application.durationInMonths")) {
            Long durationInMonth = Long.valueOf(value);
            if (durationInMonth < 1L || durationInMonth > 36L) {
                errors.add("Your project should last between 1 and 36 months");
                application.setDurationInMonths(durationInMonth);
            } else {
                application.setDurationInMonths(durationInMonth);
                applicationService.save(application);
            }
        } else if (fieldName.startsWith(APPLICATION_START_DATE)) {
            errors = this.saveApplicationStartDate(application, fieldName, value);
        } else if (fieldName.equals("application.resubmission")) {
            application.setResubmission(Boolean.valueOf(value));
            applicationService.save(application);
        } else if (fieldName.equals("application.previousApplicationNumber")) {
            application.setPreviousApplicationNumber(value);
            applicationService.save(application);
        } else if (fieldName.equals("application.previousApplicationTitle")) {
            application.setPreviousApplicationTitle(value);
            applicationService.save(application);
        }
        return errors;
    }

    private List<String> saveApplicationStartDate(ApplicationResource application, String fieldName, String value) {
        List<String> errors = new ArrayList<>();
        LocalDate startDate = application.getStartDate();
        if (fieldName.endsWith(".dayOfMonth")) {
            startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
        } else if (fieldName.endsWith(".monthValue")) {
            startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
        } else if (fieldName.endsWith(".year")) {
            startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
        } else if ("application.startDate".equals(fieldName)) {
            String[] parts = value.split("-");
            startDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        }
        if (startDate.isBefore(LocalDate.now())) {
            errors.add("Please enter a future date");
            startDate = null;
        } else {
            LOG.debug("Save startdate: " + startDate.toString());
        }

        application.setStartDate(startDate);
        applicationService.save(application);
        return errors;
    }

    private static class StoreFieldResult {
        private Long fieldId;
        private List<String> errors = new ArrayList<>();

        public StoreFieldResult() {
        }

        public StoreFieldResult(Long fieldId) {
            this.fieldId = fieldId;
        }

        public StoreFieldResult(List<String> errors) {
            this.errors = errors;
        }

        public StoreFieldResult(Long fieldId, List<String> errors) {
            this.fieldId = fieldId;
            this.errors = errors;
        }

        public List<String> getErrors() {
            return errors;
        }

        public Long getFieldId() {
            return fieldId;
        }
    }

    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping(value = "/add_cost/{" + QUESTION_ID + "}")
    public String addCostRow(@ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable(APPLICATION_ID) final Long applicationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             UserResource user) {
        FinanceRowItem costItem = addCost(applicationId, questionId, user);
        FinanceRowType costType = costItem.getCostType();
        Long organisationId = userService.getUserOrganisationId(user.getId(), applicationId);

        Set<Long> markedAsComplete = new TreeSet<>();
        model.addAttribute("markedAsComplete", markedAsComplete);
        Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);

        financeHandler.getFinanceModelManager(organisationType).addCost(model, costItem, applicationId, organisationId, user.getId(), questionId, costType);

        form.setBindingResult(bindingResult);
        return String.format("finance/finance :: %s_row(viewmode='edit')", costType.getType());
    }

    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/remove_cost/{costId}")
    public @ResponseBody
    String removeCostRow(@PathVariable("costId") final Long costId) throws JsonProcessingException {
        financeRowRestService.delete(costId).getSuccessObjectOrThrowException();
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    private FinanceRowItem addCost(Long applicationId, Long questionId, UserResource user) {
        Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        return financeHandler.getFinanceFormHandler(organisationType).addCostWithoutPersisting(applicationId, user.getId(), questionId);
    }
}
