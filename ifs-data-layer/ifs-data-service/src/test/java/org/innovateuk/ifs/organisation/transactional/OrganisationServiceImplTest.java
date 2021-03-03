package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseType;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.KNOWLEDGE_BASE;
import static org.mockito.Mockito.when;


public class OrganisationServiceImplTest extends BaseServiceUnitTest<OrganisationService> {

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private OrganisationMapper organisationMapper;

    protected OrganisationService supplyServiceUnderTest() {
        return new OrganisationServiceImpl();
    }

    @Before
    public void setup() {
        //address = newAddress().build();
        //organisationType = newOrganisationType().withOrganisationType(KNOWLEDGE_BASE).build();
        //knowledgeBase = new KnowledgeBase(1l, "KnowledgeBase 1", "123456789", KnowledgeBaseType.CATAPULT, address);
        //knowledgeBaseResource = new KnowledgeBaseResource(1l, "KnowledgeBase 1",KnowledgeBaseType.CATAPULT, "12345678", null);
    }

    @Test
    public void syncCompaniesHouseDetails() {
        //when(organisationRepository.findById())
    }
}