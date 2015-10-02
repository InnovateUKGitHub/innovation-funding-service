package com.worth.ifs;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseUnitTestMocksTest {

    @Mock
    protected ApplicationRepository applicationRepositoryMock;

    @Mock
    protected ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    protected ProcessRoleRepository userAppRoleRepositoryMock;

    @Mock
    protected UserRepository userRepositoryMock;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}