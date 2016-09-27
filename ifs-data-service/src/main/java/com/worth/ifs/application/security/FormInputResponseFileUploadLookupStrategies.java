package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lookup strategy for FormInputResponseFileUploads for the custom Spring Security mechanism.
 */
@Component
@PermissionEntityLookupStrategies
public class FormInputResponseFileUploadLookupStrategies {

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @PermissionEntityLookupStrategy
    public FormInputResponseFileEntryResource getFormInputResponseFileEntryResource(FormInputResponseFileEntryId id) {
        FormInput formInput = formInputRepository.findOne(id.getFormInputId());
        Question question = formInput.getQuestion();

        FormInputResponse formInputResponse = null;

        if(question.hasMultipleStatuses()){ // If question has multiple statuses only the user who uploaded file is allowed to remove them.
            formInputResponse =
                    formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(id.getApplicationId(), id.getProcessRoleId(), id.getFormInputId());
        } else {                            // If question has single status then whoever has question assinged to them can edit/read files associated with the question
            // TODO: Secure this by checking it is the assinged user when editing
            List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationIdAndFormInputId(id.getApplicationId(), id.getFormInputId());
            if(formInputResponses != null && !formInputResponses.isEmpty()){ // Question with single status will only have one form input response
                formInputResponse = formInputResponses.get(0);
            }
        }

        if (formInputResponse == null) {
            return null;
        }

        if (formInputResponse.getFileEntry() == null) {
            return null;
        }

        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(formInputResponse.getFileEntry());
        return new FormInputResponseFileEntryResource(fileEntryResource, id);
    }
}
