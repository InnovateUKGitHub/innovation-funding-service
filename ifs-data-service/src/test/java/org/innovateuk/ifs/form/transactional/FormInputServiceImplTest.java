package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;

import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FormInputServiceImplTest extends BaseServiceUnitTest<FormInputServiceImpl> {

    @Test
    public void findResponsesByFormInputIdAndQuestionName() throws Exception {
        long applicationId = 1L;
        String questionName = "name";

        FormInputResponse formInputResponse = newFormInputResponse().build();
        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();

        when(formInputResponseRepositoryMock.findOneByApplicationIdAndFormInputQuestionName(applicationId, questionName))
                .thenReturn(formInputResponse);

        when(formInputResponseMapperMock.mapToResource(formInputResponse)).thenReturn(formInputResponseResource);

        ServiceResult<FormInputResponseResource> serviceResult = service.findResponseByApplicationIdAndQuestionName(applicationId, questionName);

        assertTrue(serviceResult.isSuccess());
        assertEquals(formInputResponseResource, serviceResult.getSuccessObject());

        verify(formInputResponseRepositoryMock, only()).findOneByApplicationIdAndFormInputQuestionName(applicationId,
                questionName);
    }

    @Override
    protected FormInputServiceImpl supplyServiceUnderTest() {
        return new FormInputServiceImpl();
    }
}