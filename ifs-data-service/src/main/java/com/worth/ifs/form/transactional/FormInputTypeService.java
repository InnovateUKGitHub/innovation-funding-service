package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.security.NotSecured;

public interface FormInputTypeService {
    @NotSecured("TODO")
    FormInputType findOne(Long id);
}