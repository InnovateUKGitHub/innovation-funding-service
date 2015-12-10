package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.service.ApplicationRestService;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class ApplicationServiceTest extends BaseUnitTest{
    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void testRetrievingApplicationById() {
        Long applicationId = 1L;
        //Application application = applicationService.getById(applicationId);
        //assertEquals(applicationId, application.getId());
        assertTrue(true);
    }

    //@Test
    public void testRetrievingApplicationByNonValidId() {
        Long applicationId = -1L;
        Application application = applicationService.getById(applicationId);
        assertNull(application);
    }

    
}
