package org.innovateuk.ifs.setup.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.setupStatusResourceListType;
import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.junit.Assert.assertEquals;

public class SetupStatusRestServiceTest extends BaseRestServiceUnitTest<SetupStatusRestServiceImpl> {

    private final static String SETUP_STATUS_URL = "setupStatus";

    @Override
    protected SetupStatusRestServiceImpl registerRestServiceUnderTest() {
        return new SetupStatusRestServiceImpl();
    }

    @Test
    public void testFindByTarget() {
        String targetClassName = CompetitionResource.class.getName();
        Long targetId = 5241L;
        List<SetupStatusResource> statusesFound = newSetupStatusResource().build(1);

        setupGetWithRestResultExpectations(SETUP_STATUS_URL + "/findByTarget/" + targetClassName + "/" + targetId,
                setupStatusResourceListType(),
                statusesFound);
        List<SetupStatusResource> result = service.findByTarget(targetClassName, targetId).getSuccessObjectOrThrowException();

        assertEquals(statusesFound, result);
    }

    @Test
    public void testFindByTargetAndParent() {
        String targetClassName = CompetitionResource.class.getName();
        Long targetId = 5241L;
        Long parentId = 1490L;
        List<SetupStatusResource> statusesFound = newSetupStatusResource().build(1);

        setupGetWithRestResultExpectations(SETUP_STATUS_URL + "/findByTargetAndParent/" + targetClassName + "/" + targetId + "/" + parentId,
                setupStatusResourceListType(),
                statusesFound);
        List<SetupStatusResource> result = service.findByTargetAndParent(targetClassName, targetId, parentId).getSuccessObjectOrThrowException();

        assertEquals(statusesFound, result);
    }

    @Test
    public void testFindByClassAndParent() {
        String className = CompetitionResource.class.getName();
        Long parentId = 5241L;
        List<SetupStatusResource> statusesFound = newSetupStatusResource().build(1);

        setupGetWithRestResultExpectations(SETUP_STATUS_URL + "/findByClassAndParent/" + className + "/" + parentId,
                setupStatusResourceListType(),
                statusesFound);
        List<SetupStatusResource> result = service.findByClassAndParent(className, parentId).getSuccessObjectOrThrowException();

        assertEquals(statusesFound, result);
    }

    @Test
    public void testFindSetupStatus() {
        String className = QuestionResource.class.getName();
        Long classPk = 5241L;
        List<SetupStatusResource> statusesFound = newSetupStatusResource().build(1);

        setupGetWithRestResultExpectations(SETUP_STATUS_URL + "/findSetupStatus/" + className + "/" + classPk,
                setupStatusResourceListType(),
                statusesFound);
        List<SetupStatusResource> result = service.findSetupStatus(className, classPk).getSuccessObjectOrThrowException();

        assertEquals(statusesFound, result);
    }

    @Test
    public void testSaveSetupStatus() {
        SetupStatusResource statusToSave = newSetupStatusResource().build();
        SetupStatusResource statusSaved = newSetupStatusResource().build();

        setupPostWithRestResultExpectations(SETUP_STATUS_URL + "/save",
                SetupStatusResource.class,
                statusToSave,
                statusSaved,
                HttpStatus.OK);
        SetupStatusResource result = service.saveSetupStatus(statusToSave).getSuccessObjectOrThrowException();

        assertEquals(statusSaved, result);
    }
}
