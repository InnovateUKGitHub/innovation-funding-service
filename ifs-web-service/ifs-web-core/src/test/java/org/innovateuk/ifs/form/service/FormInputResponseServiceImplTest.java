package org.innovateuk.ifs.form.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.junit.Assert.assertEquals;

public class FormInputResponseServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private FormInputResponseService service = new FormInputResponseServiceImpl();

    @Test
    public void mapResponsesToFormInputs() {

        List<FormInputResponseResource> formInputResponses = newFormInputResponseResource().
                withFormInputs(3L, 2L, 1L).
                build(3);

        Map<Long, FormInputResponseResource> response = service.mapFormInputResponsesToFormInput(formInputResponses);
        assertEquals(formInputResponses.get(0), response.get(3L));
        assertEquals(formInputResponses.get(1), response.get(2L));
        assertEquals(formInputResponses.get(2), response.get(1L));
    }
}
