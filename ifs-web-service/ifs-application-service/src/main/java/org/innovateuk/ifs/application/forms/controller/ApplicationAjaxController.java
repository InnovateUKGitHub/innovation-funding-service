package org.innovateuk.ifs.application.forms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.exception.AutoSaveElementException;
import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_ID;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntries;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntry;

/**
 * This controller will handle all Ajax requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ApplicationAjaxController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationAjaxController {

    private static final Log LOG = LogFactory.getLog(ApplicationAjaxController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

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
            LOG.debug("Got an exception on autosave : " + e.getMessage());
            errors.add(ex.getErrorMessage());
        }
    }

    private StoreFieldResult storeField(Long applicationId, Long userId, Long competitionId, String fieldName, String inputIdentifier, String value) throws NumberFormatException {
        Long formInputId = Long.valueOf(inputIdentifier);
        ValidationMessages saveErrors = formInputResponseRestService.saveQuestionResponse(userId, applicationId,
                formInputId, value, false).getSuccess();
        List<String> lookedUpErrorMessages = lookupErrorMessageResourceBundleEntries(messageSource, saveErrors);
        return new StoreFieldResult(lookedUpErrorMessages);
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

    @GetMapping(value = "/update_time_details")
    public String updateTimeDetails(Model model, @PathVariable Long applicationId) {
        model.addAttribute("updateDate", TimeZoneUtil.toUkTimeZone(now()));
        model.addAttribute("lastUpdatedText", "by you");
        model.addAttribute("applicationId", applicationId);
        return "question-type/form-elements :: updateTimeDetails";
    }
}
