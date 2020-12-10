package org.innovateuk.ifs.heukar.transactional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.heukar.mapper.HeukarPartnerOrganisationMapper;
import org.innovateuk.ifs.heukar.repository.HeukarPartnerOrganisationRepository;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class HeukarPartnerOrganisationServiceImplTest extends BaseServiceUnitTest<HeukarPartnerOrganisationService> {

    @Mock
    private HeukarPartnerOrganisationRepository heukarPartnerOrganisationRepository;

    @Mock
    private HeukarPartnerOrganisationMapper mapper;

    private HeukarPartnerOrganisation partnerOrganisation;
    private HeukarPartnerOrganisationResource resource;

    @Override
    protected HeukarPartnerOrganisationService supplyServiceUnderTest() {
        return new HeukarPartnerOrganisationServiceImpl();
    }

    @Before
    public void setup() {
        partnerOrganisation = new HeukarPartnerOrganisation();
        partnerOrganisation.setApplicationId(1L);
        partnerOrganisation.setOrganisationType(HeukarPartnerOrganisationTypeEnum.BUSINESS);
        when(heukarPartnerOrganisationRepository.findAllByApplicationId(1L)).thenReturn(newArrayList(partnerOrganisation));
        resource = new HeukarPartnerOrganisationResource();
        when(mapper.mapToResource(partnerOrganisation)).thenReturn(resource);
    }

    @Test
    public void findByApplicationId() {
        ServiceResult<List<HeukarPartnerOrganisationResource>> byApplicationId = service.findByApplicationId(1L);
        assertTrue(byApplicationId.isSuccess());
        MatcherAssert.assertThat(byApplicationId.getSuccess(), Matchers.equalTo(newArrayList(resource)));
    }

    @Test
    public void addNewPartnerOrgToApplication() {
        when(heukarPartnerOrganisationRepository.save(partnerOrganisation)).thenReturn(partnerOrganisation);
        when(mapper.mapWithApplicationIdToDomain(anyLong(), anyLong())).thenReturn(partnerOrganisation);
        ServiceResult<HeukarPartnerOrganisation> serviceResult = service.addNewPartnerOrgToApplication(1L, 1L);
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void updatePartnerOrganisation() {
        when(mapper.mapIdToDomain(anyLong())).thenReturn(partnerOrganisation);
        when(heukarPartnerOrganisationRepository.findById(anyLong())).thenReturn(Optional.of(partnerOrganisation));
        when(heukarPartnerOrganisationRepository.save(partnerOrganisation)).thenReturn(partnerOrganisation);
        ServiceResult<HeukarPartnerOrganisation> serviceResult = service.updatePartnerOrganisation(1L, 2L);
        assertTrue(serviceResult.isSuccess());
    }

    @Test
    public void deletePartnerOrganisation(){
        when(mapper.mapIdToDomain(anyLong())).thenReturn(partnerOrganisation);
        ServiceResult<Void> serviceResult = service.deletePartnerOrganisation(partnerOrganisation.getId());
        assertTrue(serviceResult.isSuccess());
    }

}