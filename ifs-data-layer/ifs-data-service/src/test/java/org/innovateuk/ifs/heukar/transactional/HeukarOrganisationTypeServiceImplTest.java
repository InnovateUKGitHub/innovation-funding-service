package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class HeukarOrganisationTypeServiceImplTest extends BaseServiceUnitTest<HeukarOrganisationTypeService> {

    @Mock
    private HeukarOrganisationRepository heukarOrganisationRepository;

    @Mock
    private OrganisationTypeMapper mapper;

    @Override
    protected HeukarOrganisationTypeService supplyServiceUnderTest() {
        return new HeukarOrganisationTypeServiceImpl();
    }

    @Before
    public void setup() {
        HeukarOrganisationType type = new HeukarOrganisationType();
        when(heukarOrganisationRepository.findAllByApplicationId(1)).thenReturn(newHashSet(type));

    }

    @Test
    public void findByApplicationId() {
        ServiceResult<Set<OrganisationTypeResource>> byApplicationId = service.findByApplicationId(1L);

        assertTrue(byApplicationId.isSuccess());
    }

}