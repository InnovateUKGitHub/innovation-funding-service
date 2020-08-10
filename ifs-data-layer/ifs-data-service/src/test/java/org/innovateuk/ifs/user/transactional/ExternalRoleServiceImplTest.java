package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class ExternalRoleServiceImplTest  extends BaseServiceUnitTest<ExternalRoleServiceImpl> {

    @InjectMocks
    private ExternalRoleService externalRoleService = new ExternalRoleServiceImpl();

    @Mock
    private UserRepository userRepository;

    @Override
    protected ExternalRoleServiceImpl supplyServiceUnderTest() {
        ExternalRoleServiceImpl externalRoleService = new ExternalRoleServiceImpl();
        ReflectionTestUtils.setField(externalRoleService, "externalUserEmailDomain", ".test");
        return externalRoleService;
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void addUserRole() {

        long userId = 1l;
        Role role = Role.KNOWLEDGE_TRANSFER_ADVISOR;
        User user = newUser().withId(userId)
                .withEmailAddress("testing@test.test").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ServiceResult<Void> result = externalRoleService.addUserRole(userId, role);
        assertTrue(result.isSuccess());

    }

}
