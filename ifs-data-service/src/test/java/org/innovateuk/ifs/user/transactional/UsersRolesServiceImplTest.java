package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UsersRolesServiceImplTest extends BaseServiceUnitTest<UsersRolesService> {

    @Override
    protected UsersRolesService supplyServiceUnderTest() {
        return new UsersRolesServiceImpl();
    }

    @Test
    public void getProcessRoleById() {

        ProcessRole processRole = newProcessRole().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();

        when(processRoleRepositoryMock.findOne(1L)).thenReturn(processRole);
        when(processRoleMapperMock.mapToResource(same(processRole))).thenReturn(processRoleResource);

        ServiceResult<ProcessRoleResource> result = service.getProcessRoleById(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResource, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findOne(1L);
    }

    @Test
    public void getProcessRolesByIds() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);
        List<Long> processRoleIds = asList(new Long[]{1L, 2L});

        when(processRoleRepositoryMock.findAll(processRoleIds)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByIds(new Long[]{1L, 2L});

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findAll(processRoleIds);
    }

    @Test
    public void getProcessRolesByApplicationId() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }

    @Test
    public void getProcessRoleByUserIdAndApplicationId() {

        ProcessRole processRole = newProcessRole().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(1L, 1L)).thenReturn(processRole);

        when(processRoleMapperMock.mapToResource(same(processRole))).thenReturn(processRoleResource);

        ServiceResult<ProcessRoleResource> result = service.getProcessRoleByUserIdAndApplicationId(1L, 1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResource, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findByUserIdAndApplicationId(1L, 1L);
    }

    @Test
    public void getProcessRolesByUserId() {

        List<ProcessRole> processRoles = newProcessRole().build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByUserId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getProcessRolesByUserId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources, result.getSuccessObject());

        verify(processRoleRepositoryMock, only()).findByUserId(1L);
    }

    @Test
    public void getAssignableProcessRolesByApplicationIdForLeadApplicant() {

        List<ProcessRole> processRoles = newProcessRole().withRole(UserRoleType.LEADAPPLICANT).build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getAssignableProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources.size(), result.getSuccessObject().size());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }

    @Test
    public void getAssignableProcessRolesByApplicationIdForCollaborator() {

        List<ProcessRole> processRoles = newProcessRole().withRole(UserRoleType.COLLABORATOR).build(2);
        List<ProcessRoleResource> processRoleResources = newProcessRoleResource().build(2);

        when(processRoleRepositoryMock.findByApplicationId(1L)).thenReturn(processRoles);

        zip(processRoles, processRoleResources, (pr, prr) -> when(processRoleMapperMock.mapToResource(same(pr))).thenReturn(prr));

        ServiceResult<List<ProcessRoleResource>> result = service.getAssignableProcessRolesByApplicationId(1L);

        assertTrue(result.isSuccess());
        assertEquals(processRoleResources.size(), result.getSuccessObject().size());

        verify(processRoleRepositoryMock, only()).findByApplicationId(1L);
    }
}
