package org.innovateuk.ifs;


import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.otherdocuments.ProjectOtherDocumentsService;
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
    protected ProjectService projectServiceMock;

    @Mock
    protected ProjectOtherDocumentsService projectOtherDocumentsServiceMock;

    @Mock
    protected OrganisationService organisationServiceMock;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        BaseBuilderAmendFunctions.clearUniqueIds();
    }
}
