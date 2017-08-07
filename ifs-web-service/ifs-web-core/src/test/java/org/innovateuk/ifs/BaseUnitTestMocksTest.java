package org.innovateuk.ifs;


import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.OtherDocumentsService;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass for all tests that require Mockito support.  At its simplest this class is simply a
 * place to store and initialise Mockito mocks.  Mocks can then be injected into particular attributes using the @InjectMocks
 * annotation.
 */
public abstract class BaseUnitTestMocksTest{

    @Mock
    protected ApplicationService applicationServiceMock;

    @Mock
    protected ProjectService projectServiceMock;

    @Mock
    protected StatusService statusServiceMock;

    @Mock
    protected OtherDocumentsService otherDocumentsServiceMock;

    @Mock
    protected OrganisationService organisationServiceMock;

    @Mock
    protected UserService userServiceMock;

    @Mock
    protected InviteRestService inviteRestServiceMock;

    @Mock
    protected InviteOrganisationRestService inviteOrganisationRestServiceMock;

    @Mock
    protected ProcessRoleService processRoleServiceMock;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        BaseBuilderAmendFunctions.clearUniqueIds();
    }
}
