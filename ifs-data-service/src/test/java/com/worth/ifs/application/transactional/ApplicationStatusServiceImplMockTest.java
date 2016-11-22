package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationStatusBuilder;
import com.worth.ifs.application.builder.ApplicationStatusResourceBuilder;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.commons.service.ServiceResult;
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
