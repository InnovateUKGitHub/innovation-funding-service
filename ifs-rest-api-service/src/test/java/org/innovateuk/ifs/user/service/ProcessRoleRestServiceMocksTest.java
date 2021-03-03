package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.processRoleResourceListType;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class ProcessRoleRestServiceMocksTest extends BaseRestServiceUnitTest<ProcessRoleRestServiceImpl> {

    private static final String PROCESS_ROLE_REST_URL = "/processrole";

    @Override
    protected ProcessRoleRestServiceImpl registerRestServiceUnderTest() {
        return new ProcessRoleRestServiceImpl();
    }

    @Test
    public void test_findProcessRoleByUserId() {
        List<ProcessRoleResource> processRoleList = newProcessRoleResource().build(10);
        Long userId = 249L;

        setupGetWithRestResultExpectations(PROCESS_ROLE_REST_URL + "/find-by-user-id/" + userId, processRoleResourceListType(), processRoleList);

        List<ProcessRoleResource> response = service.findProcessRoleByUserId(userId).getSuccess();
        assertEquals(10, response.size());
        assertEquals(processRoleList, response);
    }

    @Test
    public void userHasApplicationForCompetition() {
        Long userId = 1L;
        Long competitionId = 2L;
        Boolean expected = true;

        setupGetWithRestResultExpectations(format("%s/user-has-application-for-competition/%s/%s", PROCESS_ROLE_REST_URL, userId, competitionId), Boolean.class, expected, OK);

        Boolean response = service.userHasApplicationForCompetition(userId, competitionId).getSuccess();
        assertEquals(expected, response);
    }
}
