package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationStatusBuilder;
import org.innovateuk.ifs.application.builder.ApplicationStatusResourceBuilder;
import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.mapper.ApplicationStatusMapper;
import org.innovateuk.ifs.application.repository.ApplicationStatusRepository;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ApplicationStatusServiceImpl}
 */
public class ApplicationStatusServiceImplMockTest extends BaseServiceUnitTest<ApplicationStatusServiceImpl> {

    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @Mock
    private ApplicationStatusMapper applicationStatusMapper;

    @Override
    protected ApplicationStatusServiceImpl supplyServiceUnderTest() {
        return new ApplicationStatusServiceImpl();
    }

    @Test
    public void testGetById() {
        long applicationStatusId = 1L;
        ApplicationStatus status = ApplicationStatusBuilder.newApplicationStatus().build();
        ApplicationStatusResource resource = ApplicationStatusResourceBuilder.newApplicationStatusResource().build();
        when(applicationStatusRepository.findOne(applicationStatusId)).thenReturn(status);
        when(applicationStatusMapper.mapToResource(status)).thenReturn(resource);

        ServiceResult<ApplicationStatusResource> applicationStatus = service.getById(applicationStatusId);

        assertTrue(applicationStatus.isSuccess());
        assertEquals(applicationStatus.getSuccessObjectOrThrowException(), resource);
    }


}
