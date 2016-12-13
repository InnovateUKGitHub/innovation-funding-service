package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.PartnerOrganisationServiceImpl;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerOrganisationServiceImplTest {

    @InjectMocks
    private PartnerOrganisationServiceImpl service;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestServiceMock;

    @Test
    public void testGetPartnerOrganisations() {
        Long projectId = 123L;

        when(partnerOrganisationRestServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(newPartnerOrganisationResource().withId(123L).build(3)));

        ServiceResult<List<PartnerOrganisationResource>> result = service.getPartnerOrganisations(projectId);

        assertTrue(result.isSuccess());
    }
}
