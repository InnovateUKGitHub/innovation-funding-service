package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.security.NotSecured;

public interface FormValidatorService {
    @NotSecured("TODO")
    FormValidator findOne(Long id);
}