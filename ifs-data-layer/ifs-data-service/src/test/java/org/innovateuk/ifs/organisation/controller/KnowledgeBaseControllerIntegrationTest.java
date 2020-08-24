package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.KnowledgeBaseRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.CATAPULT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnowledgeBaseControllerIntegrationTest extends BaseControllerIntegrationTest<KnowledgeBaseController> {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    private KnowledgeBase knowledgeBase;

    private OrganisationType organisationType;

    private Address address;

    @Before
    public void setUp() throws Exception {
        address = newAddress().build();
        organisationType = newOrganisationType().withOrganisationType(CATAPULT).build();
        knowledgeBase = new KnowledgeBase(1l, "KnowledgeBase 1", "123456789", organisationType, address);
        knowledgeBaseRepository.save(knowledgeBase);

        loginSystemRegistrationUser();
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(KnowledgeBaseController controller) {
        this.controller = controller;
    }

    @Test
    public void getKnowledgeBaseNames() {
        RestResult<List<String>> result = controller.getKnowledgeBaseNames();
        assertEquals(1, result.getSuccess().size());
    }

    @Test
    public void getKnowledgeBaseName() {
        RestResult<String> result = controller.getKnowledgeBaseName(knowledgeBase.getId());
        assertEquals(knowledgeBase.getName(), result.getSuccess());
    }

    @Test
    public void getKnowledgeBaseByName() {
        RestResult<KnowledgeBaseResource> result = controller.getKnowledgeBaseByName("KnowledgeBase 1");
        assertEquals(knowledgeBase.getName(), result.getSuccess());
    }
}