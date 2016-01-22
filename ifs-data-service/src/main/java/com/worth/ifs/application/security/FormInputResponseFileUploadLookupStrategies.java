package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for FormInputResponseFileUploads for the custom Spring Security mechanism.
 */
@Component
@PermissionEntityLookupStrategies
public class FormInputResponseFileUploadLookupStrategies {

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @PermissionEntityLookupStrategy
    public FormInputResponseFileEntryResource getFormInputResponseFileEntryResource(FormInputResponseFileEntryId id) {

        FormInputResponse formInputResponse =
                formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(id.getApplicationId(), id.getProcessRoleId(), id.getFormInputId());

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
