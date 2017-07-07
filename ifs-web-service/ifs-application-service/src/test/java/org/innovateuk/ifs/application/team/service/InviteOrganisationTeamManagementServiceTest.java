package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.junit.Test;

public class InviteOrganisationTeamManagementServiceTest extends BaseServiceUnitTest<InviteOrganisationTeamManagementService> {

    protected InviteOrganisationTeamManagementService supplyServiceUnderTest() {
        return new InviteOrganisationTeamManagementService();
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