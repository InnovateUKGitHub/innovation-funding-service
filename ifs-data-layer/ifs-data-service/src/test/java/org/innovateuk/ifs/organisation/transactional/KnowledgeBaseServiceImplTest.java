package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.KnowledgeBaseMapper;
import org.innovateuk.ifs.organisation.repository.KnowledgeBaseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.CATAPULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class KnowledgeBaseServiceImplTest extends BaseServiceUnitTest<KnowledgeBaseService> {

    @Mock
    private KnowledgeBaseRepository knowledgeBaseRepository;

    private KnowledgeBase knowledgeBase;

    private KnowledgeBaseResource knowledgeBaseResource;

    private OrganisationType organisationType;

    private Address address;

    @Mock
    private KnowledgeBaseMapper KnowledgeBaseMapper;

    protected KnowledgeBaseService supplyServiceUnderTest() {
        return new KnowledgeBaseServiceImpl();
    }

    @Before
    public void setup() {
        address = newAddress().build();
        organisationType = newOrganisationType().withOrganisationType(CATAPULT).build();
        knowledgeBase = new KnowledgeBase(1l, "KnowledgeBase 1", "123456789", organisationType, address);
        knowledgeBaseResource = new KnowledgeBaseResource(1l, "KnowledgeBase 1",6l, "Catapult", "12345678", null);
    }

    @Test
    public void getKnowledgeBaseName() {
        when(knowledgeBaseRepository.findById(1L)).thenReturn(Optional.of(knowledgeBase));

        ServiceResult<String> result = service.getKnowledgeBaseName(1L);

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getName(), result.getSuccess());
    }

    @Test
    public void getKnowledgeBaseNames() {
        when(knowledgeBaseRepository.findAll()).thenReturn(singleton(knowledgeBase));

        ServiceResult<List<String>> result = service.getKnowledgeBaseNames();

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getName(), result.getSuccess().get(0));
    }

    @Test
    public void getKnowledgeBaseByName() {
        String name = "KnowledgeBase 1";
        when(knowledgeBaseRepository.findByName(name)).thenReturn(Optional.of(knowledgeBase));
        when(KnowledgeBaseMapper.mapToResource(knowledgeBase)).thenReturn(knowledgeBaseResource);

        ServiceResult<KnowledgeBaseResource> result = service.getKnowledgeBaseByName(name);

        assertTrue(result.isSuccess());
        assertEquals(knowledgeBase.getName(), result.getSuccess().getName());
    }
}