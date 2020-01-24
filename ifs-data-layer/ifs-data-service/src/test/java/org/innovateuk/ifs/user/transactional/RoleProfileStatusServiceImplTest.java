//package org.innovateuk.ifs.user.transactional;
//
//import org.innovateuk.ifs.BaseServiceUnitTest;
//import org.innovateuk.ifs.commons.service.ServiceResult;
//import org.innovateuk.ifs.user.domain.RoleProfileStatus;
//import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
//import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
//import org.innovateuk.ifs.user.resource.AgreementResource;
//import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.same;
//import static org.mockito.Mockito.*;
//
//public class RoleProfileStatusServiceImplTest extends BaseServiceUnitTest<RoleProfileStatusServiceImpl> {
//
//    @InjectMocks
//    private RoleProfileStatusService roleProfileStatusService = new RoleProfileStatusServiceImpl();
//
//    @Mock
//    private RoleProfileStatusRepository roleProfileStatusRepository;
//
//    @Mock
//    RoleProfileStatusMapper roleProfileStatusMapper;
//
//    @Override
//    protected RoleProfileStatusServiceImpl supplyServiceUnderTest() {
//        return new RoleProfileStatusServiceImpl();
//    }
//
//    @Test
//    public void update() {
//        long userId = 1l;
//
//        RoleProfileStatus roleProfileStatus = newRoleProfileStatus().build();
//        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().build();
//
//    }
//
//
//}
