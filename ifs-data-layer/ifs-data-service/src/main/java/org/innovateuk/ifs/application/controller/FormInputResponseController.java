package org.innovateuk.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/forminputresponse")
public class FormInputResponseController {

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ApplicationValidationUtil validationUtil;

    private static final Log LOG = LogFactory.getLog(FormInputResponseController.class);

    @GetMapping("/findResponsesByApplication/{applicationId}")
    public RestResult<List<FormInputResponseResource>> findResponsesByApplication(@PathVariable("applicationId") final long applicationId) {
        return formInputResponseService.findResponsesByApplication(applicationId).toGetResponse();
    }

    @GetMapping("/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}")
    public RestResult<List<FormInputResponseResource>> findByFormInputIdAndApplication(@PathVariable("formInputId") final long formInputId,
                                                                                       @PathVariable("applicationId") final long applicationId) {
        return formInputResponseService.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId).toGetResponse();
    }

    @GetMapping("/findByApplicationIdAndQuestionSetupType/{applicationId}/{questionSetupType}")
    public RestResult<FormInputResponseResource> findByApplicationIdAndQuestionSetupType(@PathVariable long applicationId,
                                                                                         @PathVariable QuestionSetupType questionSetupType) {
        return formInputResponseService.findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType).toGetResponse();
    }

    @GetMapping("/findByApplicationIdAndQuestionId/{applicationId}/{questionId}")
    public RestResult<List<FormInputResponseResource>> findByApplicationIdAndQuestionId(@PathVariable long applicationId,
                                                                                        @PathVariable long questionId) {
        return formInputResponseService.findResponseByApplicationIdAndQuestionId(applicationId, questionId).toGetResponse();
    }

    @PostMapping("/saveQuestionResponse")
    public RestResult<ValidationMessages> saveQuestionResponse(@RequestBody JsonNode jsonObj) {
        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long formInputId = jsonObj.get("formInputId").asLong();
        JsonNode ignoreEmptyNode = jsonObj.get("ignoreEmpty");
        boolean ignoreEmpty = ignoreEmptyNode != null && ignoreEmptyNode.asBoolean();
        String value = HtmlUtils.htmlUnescape(jsonObj.get("value").asText(""));

        ServiceResult<ValidationMessages> result = formInputResponseService.saveQuestionResponse(new FormInputResponseCommand(formInputId, applicationId, userId, value))
                .andOnSuccessReturn(response -> buildBindingResultWithCheckErrors(response, ignoreEmpty));

        return result.toPostWithBodyResponse();
    }

    private ValidationMessages buildBindingResultWithCheckErrors(FormInputResponse response, boolean ignoreEmpty) {
        BindingResult bindingResult = validationUtil.validateResponse(response, ignoreEmpty);
        if (bindingResult.hasErrors()) {
            LOG.debug("Got validation errors: ");
            bindingResult.getAllErrors().stream().forEach(e -> LOG.debug("Validation: " + e.getDefaultMessage()));
        }

        return new ValidationMessages(bindingResult);
    }
}
