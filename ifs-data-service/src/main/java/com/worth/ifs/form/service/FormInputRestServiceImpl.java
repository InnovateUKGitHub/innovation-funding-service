package com.worth.ifs.form.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.form.domain.FormInput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FormInputRestServiceImpl extends BaseRestService implements FormInputRestService {

    @Value("${ifs.data.service.rest.forminput}")
    String formInputRestURL;

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public FormInput getById(Long formInputId) {
        return restGet(formInputRestURL + "/" + formInputId, FormInput.class);
    }
}
