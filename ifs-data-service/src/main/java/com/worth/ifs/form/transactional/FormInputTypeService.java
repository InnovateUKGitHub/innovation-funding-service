package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.commons.security.NotSecured;

import java.util.List;

public interface FormInputTypeService {
    @NotSecured(value = "Anyone can find a form input type", mustBeSecuredByOtherServices = false)
    FormInputType findOne(Long id);

    @NotSecured(value = "Anyone can find a form input type", mustBeSecuredByOtherServices = false)
    List<FormInputType> findByTitle(String title);
}