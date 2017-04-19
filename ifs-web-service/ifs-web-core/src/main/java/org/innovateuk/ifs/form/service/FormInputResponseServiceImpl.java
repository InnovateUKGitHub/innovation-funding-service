package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;

/**
 * This class contains methods to retrieve and store {@link FormInputResponseResource} related data,
 * through the RestService {@link FormInputResponseRestService}.
 */
// TODO DW - INFUND-1555 - handle rest results
@Service
public class FormInputResponseServiceImpl implements FormInputResponseService {

    @Override
    public Map<Long, FormInputResponseResource> mapFormInputResponsesToFormInput(List<FormInputResponseResource> responses) {
        return simpleToMap(
                responses,
                response -> response.getFormInput(),
                response -> response
        );
    }
}
