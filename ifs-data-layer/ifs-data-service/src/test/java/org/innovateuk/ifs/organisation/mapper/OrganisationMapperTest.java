package org.innovateuk.ifs.organisation.mapper;


import org.innovateuk.ifs.organisation.domain.Organisation;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test to verify IFS-8904
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganisationMapperTest {

    @Mock
    OrganisationTypeMapper mockOrganiationTypeMapper;

    @InjectMocks
    private OrganisationMapper mapper = new OrganisationMapperImpl();

    @Test
    public void testMapResourceToDomain() {
        OrganisationResource or = new OrganisationResource();
        or.setId(37l);
        Organisation o = mapper.mapToDomain(or);
        assertEquals(or.getId(), o.getId());
    }

    @Test
    public void testMapDomainToResource() {
        Organisation o = new Organisation();
        o.setId(39l);
        OrganisationResource or = mapper.mapToResource(o);
        assertEquals(o.getId(), or.getId());
    }
}