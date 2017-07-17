package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.test.BaseTest;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.service.FileTemplateRenderer;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.mapper.*;
import org.innovateuk.ifs.user.repository.EthnicityRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.transactional.RoleService;
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
    protected RoleRepository roleRepositoryMock;

    @Mock
    protected RoleService roleServiceMock;

    @Mock
    protected ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    protected FileService fileServiceMock;

    @Mock
    protected RoleMapper roleMapperMock;

    @Mock
    protected ProcessRoleMapper processRoleMapperMock;

    @Mock
    protected UserMapper userMapperMock;

    @Mock
    protected FileHttpHeadersValidator fileValidatorMock;

    @Mock
    protected FileEntryRepository fileEntryRepositoryMock;

    @Mock
    protected FileEntryService fileEntryServiceMock;

    @Mock
    protected FileEntryMapper fileEntryMapperMock;

    @Mock
    protected EthnicityRepository ethnicityRepositoryMock;

    @Mock
    protected EthnicityMapper ethnicityMapperMock;

    @Mock
    protected FileTemplateRenderer rendererMock;

    @Mock
    protected FinanceUtil financeUtilMock;

    @Mock
    protected SpendProfileTableCalculator spendProfileTableCalculatorMock;

    @Mock
    protected LoggedInUserSupplier loggedInUserSupplierMock;


    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}
