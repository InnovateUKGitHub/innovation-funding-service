package com.worth.ifs.form.service;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.form.domain.FormInput;

/**
 * Interface for CRUD operations on {@link Response} related data.
 */
public interface FormInputService {
    public FormInput getOne(Long formInputId);

}
