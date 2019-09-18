package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.test.BaseTest;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.service.FileTemplateRenderer;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.mapper.AffiliationMapper;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass for all tests that require Mockito support.  At its simplest this class is simply a
 * place to store and initialise Mockito mocks.  Mocks can then be injected into particular attributes using the @InjectMocks
 * annotation.
 */
public abstract class RootUnitTestMocksTest extends BaseTest {

    @Mock
    protected AffiliationMapper affiliationMapperMock;

    @Mock
    protected ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    protected FileService fileServiceMock;

    @Mock
    protected ProcessRoleMapper processRoleMapperMock;

    @Mock
    protected UserMapper userMapperMock;

    @Mock
    protected FileEntryRepository fileEntryRepositoryMock;

    @Mock
    protected FileEntryService fileEntryServiceMock;

    @Mock
    protected FileEntryMapper fileEntryMapperMock;

    @Mock
    protected FileTemplateRenderer rendererMock;

    @Mock
    protected LoggedInUserSupplier loggedInUserSupplierMock;

    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}