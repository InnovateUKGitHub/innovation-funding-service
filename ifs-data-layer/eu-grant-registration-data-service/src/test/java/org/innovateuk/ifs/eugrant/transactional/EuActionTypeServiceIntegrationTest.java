package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.transactional.EuActionTypeService;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
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

        List<EuActionType> actionTypes = stream(euActionTypeRepository.findAll().spliterator(), false).collect(toList());

        assertEquals(result.getSuccess().get(0).getId(), actionTypes.get(0).getId());
    }

    @Test
    public void getById() {
        EuActionType actionType = new EuActionType();
        actionType.setId(1L);

        ServiceResult<EuActionTypeResource> result = euActionTypeService.getById(actionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getId(), actionType.getId());
    }
}
