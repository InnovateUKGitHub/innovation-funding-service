package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.form.resource.FormInputResponseResource;

import java.util.List;
import java.util.Map;

/**
 * Interface for CRUD operations on {@link FormInputResponseResource} related data.
 */
public interface FormInputResponseService {

    Map<Long, FormInputResponseResource> mapFormInputResponsesToFormInput(List<FormInputResponseResource> responses);

}
