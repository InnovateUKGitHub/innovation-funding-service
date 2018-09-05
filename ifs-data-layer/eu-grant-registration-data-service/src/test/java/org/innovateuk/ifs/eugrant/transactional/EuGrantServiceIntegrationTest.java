package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
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
        EuGrantResource surveyResource = newEuGrantResource()
                .build();

        ServiceResult<EuGrantResource> result = euGrantService.save(surveyResource);

        assertTrue(result.isSuccess());

        List<EuGrant> grants = newArrayList(euGrantRepository.findAll());

        assertEquals(grants.size(), 1);

        EuGrant grant = grants.get(0);

        assertEquals(grant.getId().toString(), result.getSuccess().getId());
    }

    @Test
    public void findById() {
        EuGrant grant = new EuGrant();
        grant = euGrantRepository.save(grant);

        ServiceResult<EuGrantResource> result = euGrantService.findById(grant.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getId(), grant.getId().toString());
    }

}
