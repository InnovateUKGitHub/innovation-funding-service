package com.worth.ifs.form.transactional;

import java.util.List;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.security.NotSecured;

public interface FormInputTypeService {
    @NotSecured("anyone can find a form input type")
    FormInputType findOne(Long id);

    @NotSecured("anyone can find a form input type")
    List<FormInputType> findByTitle(String title);
}