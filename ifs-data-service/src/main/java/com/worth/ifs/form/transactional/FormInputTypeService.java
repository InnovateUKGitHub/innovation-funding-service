package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.form.domain.FormInputType;

public interface FormInputTypeService {

    @NotSecured(value = "Anyone can find a form input type", mustBeSecuredByOtherServices = false)
    FormInputType findByTitle(String title);
    
}