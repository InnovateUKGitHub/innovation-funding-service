package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.repository.KnowledgeBaseRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnowledgeBaseControllerIntegrationTest extends BaseControllerIntegrationTest<KnowledgeBaseController> {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    private KnowledgeBase knowledgeBase;

    @Before
    public void setUp() throws Exception {
        knowledgeBase = new KnowledgeBase("Knowledge Base 1");
        knowledgeBaseRepository.save(knowledgeBase);

        loginSystemRegistrationUser();
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(KnowledgeBaseController controller) {
        this.controller = controller;
    }

    @Test
    public void getKnowledgeBases() {
        RestResult<List<String>> result = controller.getKnowledgeBases();
        assertEquals(1, result.getSuccess().size());
    }

    @Test
    public void getKnowledgeBase() {
        RestResult<String> result = controller.getKnowledgeBase(knowledgeBase.getId());
        assertEquals(knowledgeBase.getName(), result.getSuccess());
    }

    @Test
    public void createKnowledgeBase() {
        UserResource systemMaintainer = newUserResource().withRoleGlobal(Role.SYSTEM_MAINTAINER).build();
        setLoggedInUser(systemMaintainer);
        RestResult<Long> result = controller.createKnowledgeBase("Knowledge Base 2");
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteKnowledgeBase() {
        UserResource systemMaintainer = newUserResource().withRoleGlobal(Role.SYSTEM_MAINTAINER).build();
        setLoggedInUser(systemMaintainer);
        RestResult<Void> result = controller.deleteKnowledgeBase(knowledgeBase.getId());
        assertTrue(result.isSuccess());
    }
}