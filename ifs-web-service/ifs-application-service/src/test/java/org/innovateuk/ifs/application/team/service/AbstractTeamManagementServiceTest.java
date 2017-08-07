package org.innovateuk.ifs.application.team.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.team.form.ApplicationTeamUpdateForm;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;

public class AbstractTeamManagementServiceTest extends BaseServiceUnitTest<AbstractTeamManagementService> {
    protected AbstractTeamManagementService supplyServiceUnderTest() {
        return new ExtendedAbstractTeamManagementService();
    }

    @Test
    public void removeInvite_shouldCallRemoveCollaboratorAndReturnResult() throws Exception {
        long applicationInviteId = 1L;

        when(applicationServiceMock.removeCollaborator(applicationInviteId)).thenReturn(serviceSuccess());
        service.removeInvite(applicationInviteId);

        verify(applicationServiceMock, times(1)).removeCollaborator(applicationInviteId);
    }

    private class ExtendedAbstractTeamManagementService extends AbstractTeamManagementService {
        @Override
        public boolean applicationAndOrganisationIdCombinationIsValid(Long applicationId, Long organisationId) {
            return false;
        }

        @Override
        public ApplicationTeamManagementViewModel createViewModel(long applicationId, long organisationId, UserResource loggedInUser) {
            return null;
        }

        @Override
        public ServiceResult<InviteResultsResource> executeStagedInvite(long applicationId, long organisationId, ApplicationTeamUpdateForm form) {
            return null;
        }

        @Override
        public List<Long> getInviteIds(long applicationId, long organisationId) {
            return null;
        }
    }
}
