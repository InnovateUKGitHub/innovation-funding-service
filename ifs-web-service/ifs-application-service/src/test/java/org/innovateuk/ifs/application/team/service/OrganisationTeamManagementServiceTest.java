package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.junit.Test;

public class OrganisationTeamManagementServiceTest extends BaseServiceUnitTest<OrganisationTeamManagementService> {

    protected OrganisationTeamManagementService supplyServiceUnderTest() {
        return new OrganisationTeamManagementService();
    }

    @Test
    public void createViewModel_populatorShouldBeAppropriateParameters() throws Exception {

    }

    @Test
    public void executeStagedInvite_callSaveInviteShouldBeCalledWithCorrectlyMappedInvite() throws Exception {

    }

    @Test
    public void validateOrganisationAndApplicationIds_supplierOutcomeShouldBeReturnedWhenTheOrganisationIsFound() throws Exception {

    }

    @Test
    public void validateOrganisationAndApplicationIds_exceptionShouldBeThrownWhenTheOrganisationIsNotFound() throws Exception {

    }

    @Test
    public void getInviteIds_foundIdsShouldBeMappedToReturnedList() throws Exception {

    }

    @Test
    public void getInviteIds_noIdsFoundsShouldReturnEmptyList() throws Exception {

    }

}