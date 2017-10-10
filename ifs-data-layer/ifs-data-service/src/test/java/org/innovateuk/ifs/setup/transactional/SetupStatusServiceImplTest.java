package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.mapper.SetupStatusMapper;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.setup.builder.SetupStatusBuilder.newSetupStatus;
import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetupStatusServiceImplTest extends BaseServiceUnitTest<SetupStatusServiceImpl> {

    @Mock
    private SetupStatusRepository setupStatusRepository;

    @Mock
    private SetupStatusMapper setupStatusMapper;

    @Override
    protected SetupStatusServiceImpl supplyServiceUnderTest() {
        return new SetupStatusServiceImpl();
    }

    @Test
    public void testFindByTargetIdAndTargetClassName() {
        final String targetClassName = Question.class.getName();
        final Long targetId = 23L;
        final List<SetupStatus> setupStatus = newSetupStatus()
                .withId(1L)
                .withTargetId(targetId)
                .withTargetClassName(targetClassName)
                .build(1);
        final List<SetupStatusResource> setupStatusResource = newSetupStatusResource()
                .withId(1L)
                .withTargetId(targetId)
                .withTargetClassName(targetClassName)
                .build(1);

        when(setupStatusRepository.findByTargetClassNameAndTargetId(targetClassName, targetId)).thenReturn(setupStatus);
        when(setupStatusMapper.mapToResource(setupStatus)).thenReturn(setupStatusResource);

        ServiceResult<List<SetupStatusResource>> serviceResult = service.findByTargetClassNameAndTargetId(targetClassName, targetId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResource, serviceResult.getSuccessObject());
    }

    @Test
    public void testFindByTargetClassNameAndParentId() {
        final String className = Competition.class.getName();
        final Long parentId = 8234L;

        List<SetupStatus> setupStatuses = newSetupStatus()
                .withId(1L, 2L)
                .withTargetClassName(className, className)
                .withParentId(parentId, parentId)
                .build(2);

        List<SetupStatusResource> setupStatusResources = newSetupStatusResource()
                .withId(1L, 2L)
                .withTargetClassName(className, className)
                .withParentId(parentId, parentId)
                .build(2);
        when(setupStatusRepository.findByClassNameAndParentId(className, parentId)).thenReturn(setupStatuses);
        when(setupStatusMapper.mapToResource(setupStatuses)).thenReturn(setupStatusResources);

        ServiceResult<List<SetupStatusResource>> serviceResult = service.findByClassNameAndParentId(className, parentId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResources, serviceResult.getSuccessObject());
    }

    @Test
    public void testFindByTargetClassNameAndTargetIdAndParentId() {
        final String targetClassName = Competition.class.getName();
        final Long targetId = 2314L;
        final Long parentId = 8234L;

        List<SetupStatus> setupStatuses = newSetupStatus()
                .withId(1L, 2L)
                .withTargetId(targetId, targetId)
                .withTargetClassName(targetClassName, targetClassName)
                .withParentId(parentId, parentId)
                .build(2);

        List<SetupStatusResource> setupStatusResources = newSetupStatusResource()
                .withId(1L, 2L)
                .withTargetId(targetId, targetId)
                .withTargetClassName(targetClassName, targetClassName)
                .withParentId(parentId, parentId)
                .build(2);
        when(setupStatusRepository.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId)).thenReturn(setupStatuses);
        when(setupStatusMapper.mapToResource(setupStatuses)).thenReturn(setupStatusResources);

        ServiceResult<List<SetupStatusResource>> serviceResult = service.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResources, serviceResult.getSuccessObject());
    }

    @Test
    public void findSetupStatus() {
        final Long classPk = 32L;
        final String className = Question.class.getName();
        final SetupStatus setupStatus = newSetupStatus()
                .withId(1L)
                .withCompleted(Boolean.FALSE)
                .withClassPk(classPk)
                .withClassName(className)
                .build();

        final SetupStatusResource setupStatusResource = newSetupStatusResource()
                .withId(1L)
                .withCompleted(Boolean.FALSE)
                .withClassPk(classPk)
                .withClassName(className)
                .build();

        when(setupStatusRepository.findByClassNameAndClassPk(className, classPk)).thenReturn(setupStatus);
        when(setupStatusMapper.mapToResource(setupStatus)).thenReturn(setupStatusResource);

        ServiceResult<SetupStatusResource> serviceResult = service.findSetupStatus(className, classPk);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResource, serviceResult.getSuccessObject());
    }

    @Test
    public void findSetupStatusAndTarget() {
        final Long classPk = 32L;
        final String className = Question.class.getName();
        final Long targetId = 492L;
        final String targetClassName = Competition.class.getName();

        final SetupStatus setupStatus = newSetupStatus()
                .withId(1L)
                .withCompleted(Boolean.FALSE)
                .withClassPk(classPk)
                .withClassName(className)
                .withTargetClassName(targetClassName)
                .withTargetId(targetId)
                .build();

        final SetupStatusResource setupStatusResource = newSetupStatusResource()
                .withId(1L)
                .withCompleted(Boolean.FALSE)
                .withClassPk(classPk)
                .withClassName(className)
                .withTargetClassName(targetClassName)
                .withTargetId(targetId)
                .build();

        when(setupStatusRepository.findByClassNameAndClassPkAndTargetClassNameAndTargetId(className, classPk, targetClassName, targetId)).thenReturn(setupStatus);
        when(setupStatusMapper.mapToResource(setupStatus)).thenReturn(setupStatusResource);

        ServiceResult<SetupStatusResource> serviceResult = service.findSetupStatusAndTarget(className, classPk, targetClassName, targetId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResource, serviceResult.getSuccessObject());
    }

    @Test
    public void testSaveSetupStatus() {
        final Long classPk = 32L;
        final String className = Question.class.getName();
        final SetupStatus setupStatus = newSetupStatus()
                .withId(1L)
                .withCompleted(Boolean.TRUE)
                .withClassPk(classPk)
                .withClassName(className)
                .build();
        final SetupStatusResource setupStatusResource = newSetupStatusResource()
                .withId(1L)
                .withCompleted(Boolean.TRUE)
                .withClassPk(classPk)
                .withClassName(className)
                .build();
        when(setupStatusMapper.mapToDomain(setupStatusResource)).thenReturn(setupStatus);
        when(setupStatusMapper.mapToResource(setupStatus)).thenReturn(setupStatusResource);
        when(setupStatusRepository.save(setupStatus)).thenReturn(setupStatus);

        ServiceResult<SetupStatusResource> serviceResult = service.saveSetupStatus(setupStatusResource);

        assertTrue(serviceResult.isSuccess());
        assertEquals(setupStatusResource, serviceResult.getSuccessObject());
    }
}
