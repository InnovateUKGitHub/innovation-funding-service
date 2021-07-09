package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.innovateuk.ifs.application.mapper.ApplicationExternalConfigMapper;
import org.innovateuk.ifs.application.repository.ApplicationExternalConfigRepository;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationExternalConfigResourceBuilder.newApplicationExternalConfigResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationExternalConfigServiceImplTest extends BaseServiceUnitTest<ApplicationExternalConfigServiceImpl> {

    @Mock
    private ApplicationExternalConfigRepository applicationExternalConfigRepository;

    @Mock
    private ApplicationExternalConfigMapper mapper;

    @Override
    protected ApplicationExternalConfigServiceImpl supplyServiceUnderTest() {
        return new ApplicationExternalConfigServiceImpl();
    }

    private long applicationId;
    private ApplicationExternalConfig config;

    @Before
    public void setup() {
        applicationId = 100L;
        config = new ApplicationExternalConfig(newApplication().
                withId(applicationId).build(), null, null);
    }

    @Test
    public void findOneByApplicationId() {
        ApplicationExternalConfigResource resource = new ApplicationExternalConfigResource();

        when(applicationExternalConfigRepository.findOneByApplicationId(applicationId)).thenReturn(Optional.of(config));
        when(mapper.mapToResource(config)).thenReturn(resource);

        ServiceResult<ApplicationExternalConfigResource> result = service.findOneByApplicationId(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(config.getId(), result.getSuccess().getId());
    }

    @Test
    public void updateExternalApplicationData() {
        String externalAppId= "Test external application 123";
        String externalApplicantName = "Mr.Test External Applicant";

       ApplicationExternalConfigResource resource =  newApplicationExternalConfigResource()
                        .withExternalApplicationId(externalAppId)
                        .withExternalApplicantName(externalApplicantName)
                        .build();

        when(applicationExternalConfigRepository.findOneByApplicationId(applicationId)).thenReturn(Optional.of(config));
        ServiceResult<Void> result = service.update(applicationId, resource);

        assertTrue(result.isSuccess());
        assertEquals(externalAppId, config.getExternalApplicationId());
        assertEquals(externalApplicantName, config.getExternalApplicantName());

    }
}
