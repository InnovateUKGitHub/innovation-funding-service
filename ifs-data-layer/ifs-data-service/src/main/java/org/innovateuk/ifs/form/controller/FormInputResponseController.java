package org.innovateuk.ifs.form.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.FormInputResponseCommand;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.validator.util.ValidationUtil;
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
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private ValidationUtil validationUtil;

    private static final Log LOG = LogFactory.getLog(FormInputResponseController.class);

    @GetMapping("/findResponsesByApplication/{applicationId}")
    public RestResult<List<FormInputResponseResource>> findResponsesByApplication(@PathVariable("applicationId") final Long applicationId){
        return formInputService.findResponsesByApplication(applicationId).toGetResponse();
    }

    @GetMapping("/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}")
    public RestResult<List<FormInputResponseResource>> findByFormInputIdAndApplication(@PathVariable("formInputId") final Long formInputId, @PathVariable("applicationId") final Long applicationId){
        return formInputService.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId).toGetResponse();
    }

    @GetMapping("/findByApplicationIdAndQuestionName/{applicationId}/{questionName}")
    public RestResult<FormInputResponseResource> findByApplicationIdAndQuestionName(@PathVariable long applicationId,
                                                                                 @PathVariable String questionName) {
        return formInputService.findResponseByApplicationIdAndQuestionName(applicationId, questionName).toGetResponse();
    }

    @GetMapping("/findByApplicationIdAndQuestionId/{applicationId}/{questionId}")
    public RestResult<List<FormInputResponseResource>> findByApplicationIdAndQuestionId(@PathVariable long applicationId,
                                                                                  @PathVariable long questionId) {
        return formInputService.findResponseByApplicationIdAndQuestionId(applicationId, questionId).toGetResponse();
    }

    @PostMapping("/saveQuestionResponse")
    public RestResult<ValidationMessages> saveQuestionResponse(@RequestBody JsonNode jsonObj) {

        Long userId = jsonObj.get("userId").asLong();
        Long applicationId = jsonObj.get("applicationId").asLong();
        Long formInputId = jsonObj.get("formInputId").asLong();
        JsonNode ignoreEmptyNode = jsonObj.get("ignoreEmpty");
        Boolean ignoreEmpty = ignoreEmptyNode != null && ignoreEmptyNode.asBoolean();
        String value = HtmlUtils.htmlUnescape(jsonObj.get("value").asText(""));

        ServiceResult<ValidationMessages> result = formInputService.saveQuestionResponse(new FormInputResponseCommand(formInputId, applicationId,  userId, value)).andOnSuccessReturn(response -> {

            BindingResult bindingResult = validationUtil.validateResponse(response, ignoreEmpty);
            if (bindingResult.hasErrors()) {
                LOG.debug("Got validation errors: ");
                bindingResult.getAllErrors().stream().forEach(e -> LOG.debug("Validation: " + e.getDefaultMessage()));
            }

            formInputResponseRepository.save(response);
            LOG.debug("Single question saved!");

            return new ValidationMessages(bindingResult);
        });

        return result.toPostWithBodyResponse();
    }
}
