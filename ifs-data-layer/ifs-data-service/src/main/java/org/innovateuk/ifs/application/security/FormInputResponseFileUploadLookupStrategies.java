package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
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
        FormInput formInput = formInputRepository.findById(id.getFormInputId()).get();
        Question question = formInput.getQuestion();

        FormInputResponse formInputResponse = null;

        if(question.hasMultipleStatuses()){ // If question has multiple statuses only the user who uploaded files is allowed to remove them.
            formInputResponse =
                    formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(id.getApplicationId(),
                                                                                                id.getProcessRoleId(),
                                                                                                id.getFormInputId());
        } else {                            // If question has single status then whoever has the question assigned to them can edit/read files associated with the question

            List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationIdAndFormInputId(id.getApplicationId(), id.getFormInputId());
            if(formInputResponses != null && !formInputResponses.isEmpty()){ // Question with single status will only have one form input response
                formInputResponse = formInputResponses.get(0);
            }
        }

        if (formInputResponse == null || formInputResponse.getFileEntries().isEmpty())  {
            return null;
        }

        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(formInputResponse.getFileEntries().get(0));
        return new FormInputResponseFileEntryResource(fileEntryResource, id);
    }
}
