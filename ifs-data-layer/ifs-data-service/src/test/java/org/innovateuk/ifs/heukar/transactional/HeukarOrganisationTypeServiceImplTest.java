package org.innovateuk.ifs.heukar.transactional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
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

    private HeukarOrganisationType type;

    @Override
    protected HeukarOrganisationTypeService supplyServiceUnderTest() {
        return new HeukarOrganisationTypeServiceImpl();
    }

    @Before
    public void setup() {
        type = new HeukarOrganisationType();
        type.setApplicationId(1L);
        type.setOrganisationType(new OrganisationType());
        when(heukarOrganisationRepository.findAllByApplicationId(1L)).thenReturn(newHashSet(type));

    }

    @Test
    public void findByApplicationId() {
        ServiceResult<Set<OrganisationTypeResource>> byApplicationId = service.findByApplicationId(1L);

        assertTrue(byApplicationId.isSuccess());
//        MatcherAssert.assertThat(byApplicationId.getSuccess(), Matchers.hasSize(type.getOrganisationTypes().size()));
    }

}