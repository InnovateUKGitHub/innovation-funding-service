package com.worth.ifs.application.security;


import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class FormInputResponseFileUploadLookup {

    @Autowired
    private FormInputResponseRepository responseRepository;

    @PermissionEntityLookupStrategy
    public FormInputResponse getFormInputResponse(Long formInputResponseId) {
        return responseRepository.findOne(formInputResponseId);
    }
}
