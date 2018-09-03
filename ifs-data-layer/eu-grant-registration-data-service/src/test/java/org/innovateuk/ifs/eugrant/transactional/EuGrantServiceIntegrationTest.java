package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.eugrant.builder.EuContactResourceBuilder.newEuContactResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EuGrantServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EuGrantService euGrantService;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Before
    public void cleanRepository() {
        euGrantRepository.deleteAll();
    }

    @Test
    public void save() {

        EuOrganisationResource euOrganisationResource = newEuOrganisationResource()
                .withName("worth")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .withCompaniesHouseNumber("1234")
                .build();

        EuContactResource euContactResource = newEuContactResource()
                .withName("Worth")
                .withEmail("Worth@gmail.com")
                .withJobTitle("worth employee")
                .withTelephone("0123456789")
                .build();

        EuGrantResource euGrantResource = newEuGrantResource()
                .withOrganisation(euOrganisationResource)
                .withContact(euContactResource)
                .build();

        ServiceResult<Void> result = euGrantService.save(euGrantResource);

        assertTrue(result.isSuccess());

        List<EuGrant> grants = newArrayList(euGrantRepository.findAll());

        assertEquals(grants.size(), 1);

        EuGrant grant = grants.get(0);

        assertEquals(grant.getContact().getName(), euGrantResource.getContact().getName());
        assertEquals(grant.getContact().getJobTitle(), euGrantResource.getContact().getJobTitle());
        assertEquals(grant.getContact().getEmail(), euGrantResource.getContact().getEmail());
        assertEquals(grant.getContact().getTelephone(), euGrantResource.getContact().getTelephone());

        assertEquals(grant.getOrganisation().getName(), euGrantResource.getOrganisation().getName());
        assertEquals(grant.getOrganisation().getOrganisationType(), euGrantResource.getOrganisation().getOrganisationType());
        assertEquals(grant.getOrganisation().getCompaniesHouseNumber(), euGrantResource.getOrganisation().getCompaniesHouseNumber());
    }

    @Test
    public void findById() {
        EuGrant grant = new EuGrant();
        grant = euGrantRepository.save(grant);

        ServiceResult<EuGrantResource> result = euGrantService.findById(grant.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getId().toString(), grant.getId().toString());
    }

    @Test
    public void create() throws Exception {
        ServiceResult<EuGrantResource> result = euGrantService.create();

        List<EuGrant> grants = newArrayList(euGrantRepository.findAll());

        assertTrue(result.isSuccess());
        assertFalse(grants.isEmpty());
    }
}
