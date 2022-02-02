package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.junit.Assert.assertEquals;

public class RoleProfileStatusRestServiceMocksTest extends BaseRestServiceUnitTest<RoleProfileStatusRestServiceImpl> {

    private static final String USERS_URL = "/user";

    private static final String filter = "filter";
    private static final String sort = null;
    private static final int pageNumber = 0;
    private static final int pageSize = 40;

    @Override
    protected RoleProfileStatusRestServiceImpl registerRestServiceUnderTest() {
        return new RoleProfileStatusRestServiceImpl();
    }

    @Test
    public void getAvailableAssessors() {
        UserPageResource expected = setupRoleProfileExpectation(RoleProfileState.ACTIVE);

        UserPageResource actual = service.getAvailableAssessors(filter, pageNumber, pageSize).getSuccess();

        assertEquals(expected, actual);
    }


    @Test
    public void getUnavailableAssessors() {
        UserPageResource expected = setupRoleProfileExpectation(RoleProfileState.UNAVAILABLE);

        UserPageResource actual = service.getUnavailableAssessors(filter, pageNumber, pageSize).getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getDisabledAssessors() {
        UserPageResource expected = setupRoleProfileExpectation(RoleProfileState.DISABLED);

        UserPageResource actual = service.getDisabledAssessors(filter, pageNumber, pageSize).getSuccess();

        assertEquals(expected, actual);
    }

    private UserPageResource setupRoleProfileExpectation(RoleProfileState roleProfileState) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);

        String url = buildPaginationUri(format("%s/role-profile-status/%s/%s", USERS_URL, roleProfileState, ProfileRole.ASSESSOR),
                pageNumber, pageSize, sort, params);

        UserPageResource expected = new UserPageResource();

        setupGetWithRestResultExpectations(url, UserPageResource.class, expected);

        return expected;
    }
}