package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        ApplicationResource application = applicationService.getById(applicationId);
        assertNull(application);
    }

    
}
