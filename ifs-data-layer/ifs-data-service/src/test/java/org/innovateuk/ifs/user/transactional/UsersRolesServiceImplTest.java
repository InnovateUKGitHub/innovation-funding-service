package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.applicantProcessRoles;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UsersRolesServiceImplTest extends BaseServiceUnitTest<UsersRolesService> {

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ProcessRoleMapper processRoleMapperMock;

    @Override
    protected UsersRolesService supplyServiceUnderTest() {
        return new UsersRolesServiceImpl();
    }

    @Test
    public void getProcessRoleById() {

        ProcessRole processRole = newProcessRole().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();

        when(processRoleRepositoryMock.findById(1L)).thenReturn(Optional.of(processRole));
        when(processRoleMapperMock.mapToResource(same(processRole))).thenReturn(processRoleResource);

        ServiceResult<ProcessRoleResource> result = service.getProcessRoleById(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResource, result.getSuccess());

        verify(processRoleRepositoryMock, only()).findById(1L);
    }

    @Test
    public void getProcessRolesByIds() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);
        List<Long> processRoleIds = asList(new Long[]{1L, 2L});

        when(processRoleRepositoryMock.findAllById(processRoleIds)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByIds(new Long[]{1L, 2L});

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccess());

        verify(processRoleRepositoryMock, only()).findAllById(processRoleIds);
    }

    @Test
    public void getProcessRolesByApplicationId() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccess());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }

    @Test
    public void getProcessRoleByUserIdAndApplicationId() {

        ProcessRole processRole = newProcessRole().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();

        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(1L, applicantProcessRoles(), 1L)).thenReturn(processRole);

        when(processRoleMapperMock.mapToResource(same(processRole))).thenReturn(processRoleResource);

        ServiceResult<ProcessRoleResource> result = service.getProcessRoleByUserIdAndApplicationId(1L, 1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResource, result.getSuccess());

        verify(processRoleRepositoryMock, only()).findOneByUserIdAndRoleInAndApplicationId(1L, applicantProcessRoles(), 1L);
    }

    @Test
    public void getProcessRolesByUserId() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByUserId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByUserId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccess());

        verify(processRoleRepositoryMock, only()).findByUserId(1L);
    }

    @Test
    public void getAssignableProcessRolesByApplicationIdForLeadApplicant() {

        List<ProcessRole> processRoles = newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT).build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getAssignableProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources.size(), result.getSuccess().size());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }

    @Test
    public void getAssignableProcessRolesByApplicationIdForCollaborator() {

        List<ProcessRole> processRoles = newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT).build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getAssignableProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources.size(), result.getSuccess().size());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }
}
