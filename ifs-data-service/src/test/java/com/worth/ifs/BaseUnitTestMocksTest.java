package com.worth.ifs;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.resourceAssembler.ApplicationResourceAssembler;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.ServiceLocator;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass for all tests that require Mockito support.  At its simplest this class is simply a
 * place to store and initialise Mockito mocks.  Mocks can then be injected into particular attributes using the @InjectMocks
 * annotation.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseUnitTestMocksTest {

    @Mock
    protected ApplicationRepository applicationRepositoryMock;

    @Mock
    protected ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    protected FormInputResponseRepository formInputResponseRepository;

    @Mock
    protected UserRepository userRepositoryMock;

    @Mock
    protected RoleRepository roleRepositoryMock;

    @Mock
    protected ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    protected ResponseRepository responseRepositoryMock;

    @Mock
    protected ApplicationResourceAssembler applicationResourceAssembler;

    @Mock
    protected CompetitionsRepository competitionsRepositoryMock;

    @Mock
    protected OrganisationRepository organisationRepositoryMock;

    @Mock
    protected ApplicationStatusRepository applicationStatusRepositoryMock;

    @Mock
    protected FormInputRepository formInputRepository;

    @InjectMocks
    protected ServiceLocator serviceLocator = new ServiceLocator();

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}