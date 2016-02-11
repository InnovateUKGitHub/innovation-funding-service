package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface FormInputTypeService {
    @NotSecured("TODO")
    FormInputType findOne(Long id);

    @NotSecured("TODO")
    List<FormInputType> findByTitle(String title);
}