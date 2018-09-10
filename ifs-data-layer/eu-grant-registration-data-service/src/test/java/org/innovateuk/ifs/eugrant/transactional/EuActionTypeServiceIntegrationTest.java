package org.innovateuk.ifs.eugrant.transactional;

import com.drew.lang.Iterables;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.eugrant.domain.EuActionType;
import org.innovateuk.ifs.eugrant.repository.EuActionTypeRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EuActionTypeServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EuActionTypeService euActionTypeService;

    @Autowired
    private EuActionTypeRepository euActionTypeRepository;

    @Test
    public void findAll() {
        ServiceResult<List<EuActionTypeResource>> result = euActionTypeService.findAll();
        assertTrue(result.isSuccess());

        List<EuActionType> actionTypes = Iterables.toList(euActionTypeRepository.findAll());

        assertEquals(result.getSuccess().get(0).getId(), actionTypes.get(0).getId());
    }

    @Test
    public void getById() {
        EuActionType actionType = new EuActionType();
        ServiceResult<EuActionTypeResource> result = euActionTypeService.getById(actionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getId(), actionType.getId());
    }
}
