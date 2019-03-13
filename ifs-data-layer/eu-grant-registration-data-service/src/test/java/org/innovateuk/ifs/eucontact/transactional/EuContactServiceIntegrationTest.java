package org.innovateuk.ifs.eucontact.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eucontact.repository.EuContactRepository;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.innovateuk.ifs.eugrant.domain.EuContactBuilder.newEuContact;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EuContactServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EuContactService euContactService;

    @Autowired
    private EuContactRepository euContactRepository;

    @Test
    public void getByNotified() {

        EuContact euContactOne = newEuContact()
                .withName("Barry Venison")
                .withEmail("barry@venison.com")
                .withJobTitle("Rollercoaster operator")
                .withTelephone("2468")
                .withNotified(false)
                .build();

        EuContact euContactTwo = newEuContact()
                .withName("Garry Owen")
                .withEmail("garry@owen.com")
                .withJobTitle("Secretary")
                .withTelephone("1357")
                .withNotified(true)
                .build();

        euContactRepository.save(euContactOne);
        euContactRepository.save(euContactTwo);

        Pageable pageable = new PageRequest(0, 100 , new Sort("id"));

        ServiceResult<EuContactPageResource> resultOne = euContactService.getEuContactsByNotified(false, pageable);
        ServiceResult<EuContactPageResource> resultTwo = euContactService.getEuContactsByNotified(true, pageable);

        assertTrue(resultOne.isSuccess());

        assertEquals(1, resultOne.getSuccess().getContent().size());
        EuContactResource resultingResource = resultOne.getSuccess().getContent().get(0);
        assertEquals(euContactOne.getEmail(), resultingResource.getEmail());
        assertEquals(euContactOne.getJobTitle(), resultingResource.getJobTitle());
        assertEquals(euContactOne.getName(), resultingResource.getName());
        assertEquals(euContactOne.getTelephone(), resultingResource.getTelephone());
        assertEquals(euContactOne.getNotified(), resultingResource.getNotified());

        assertTrue(resultTwo.isSuccess());

        assertEquals(1, resultTwo.getSuccess().getContent().size());
        EuContactResource resultingResourceTwo = resultTwo.getSuccess().getContent().get(0);
        assertEquals(euContactTwo.getEmail(), resultingResourceTwo.getEmail());
        assertEquals(euContactTwo.getJobTitle(), resultingResourceTwo.getJobTitle());
        assertEquals(euContactTwo.getName(), resultingResourceTwo.getName());
        assertEquals(euContactTwo.getTelephone(), resultingResourceTwo.getTelephone());
        assertEquals(euContactTwo.getNotified(), resultingResourceTwo.getNotified());
    }
}
