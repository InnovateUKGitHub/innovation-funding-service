package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.innovateuk.ifs.heukar.repository.HeukarOrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HeukarOrganisationTypeServiceImplTest extends BaseServiceUnitTest<HeukarOrganisationTypeService> {

    @Mock
    private HeukarOrganisationRepository heukarOrganisationRepository;

    @Override
    protected HeukarOrganisationTypeService supplyServiceUnderTest() {
        return new HeukarOrganisationTypeServiceImpl();
    }

    @Before
    public void setup() {
        HeukarOrganisationType type = new HeukarOrganisationType(1, 1L, 1L);
        when(heukarOrganisationRepository.findAllByApplicationId(1)).thenReturn(newHashSet(type));

    }

    @Test
    public void findByApplicationId() {
        ServiceResult<Set<HeukarOrganisationType>> byApplicationId = service.findByApplicationId(1L);

        assertTrue(byApplicationId.isSuccess());
    }

}