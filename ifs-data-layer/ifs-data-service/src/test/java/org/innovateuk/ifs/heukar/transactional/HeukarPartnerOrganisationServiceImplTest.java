package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.heukar.repository.HeukarPartnerOrganisationRepository;
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

public class HeukarPartnerOrganisationServiceImplTest extends BaseServiceUnitTest<HeukarPartnerOrganisationService> {

    @Mock
    private HeukarPartnerOrganisationRepository heukarPartnerOrganisationRepository;

    @Mock
    private OrganisationTypeMapper mapper;

    private HeukarPartnerOrganisation type;

    @Override
    protected HeukarPartnerOrganisationService supplyServiceUnderTest() {
        return new HeukarPartnerOrganisationServiceImpl();
    }

    @Before
    public void setup() {
        type = new HeukarPartnerOrganisation();
        type.setApplicationId(1L);
        type.setOrganisationType(new OrganisationType());
        when(heukarPartnerOrganisationRepository.findAllByApplicationId(1L)).thenReturn(newHashSet(type));

    }

    @Test
    public void findByApplicationId() {
//        ServiceResult<Set<OrganisationTypeResource>> byApplicationId = service.findByApplicationId(1L);

//        assertTrue(byApplicationId.isSuccess());
//        MatcherAssert.assertThat(byApplicationId.getSuccess(), Matchers.hasSize(type.getOrganisationTypes().size()));
    }

}