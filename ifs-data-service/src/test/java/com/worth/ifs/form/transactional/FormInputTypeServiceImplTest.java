package com.worth.ifs.form.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.form.domain.FormInputType;
import org.junit.Test;

import static com.worth.ifs.form.builder.FormInputTypeBuilder.newFormInputType;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FormInputTypeServiceImplTest extends BaseServiceUnitTest<FormInputTypeService> {

    @Override
    protected FormInputTypeService supplyServiceUnderTest() {
        return new FormInputTypeServiceImpl();
    }

    @Test
    public void findByTitle() throws Exception {
        String title = "test_type";
        FormInputType expectedType = newFormInputType().build();

        when(formInputTypeRepositoryMock.findByTitle(title)).thenReturn(expectedType);

        assertEquals(expectedType, service.findByTitle(title));
        verify(formInputTypeRepositoryMock, only()).findByTitle(title);
    }
}