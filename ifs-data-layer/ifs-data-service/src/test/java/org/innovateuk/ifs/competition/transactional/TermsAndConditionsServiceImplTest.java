package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competition.mapper.GrantTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.mapper.SiteTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.SiteTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class TermsAndConditionsServiceImplTest extends BaseServiceUnitTest<TermsAndConditionsServiceImpl> {

    @Mock
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Mock
    private SiteTermsAndConditionsRepository siteTermsAndConditionsRepository;

    @Mock
    private GrantTermsAndConditionsMapper grantTermsAndConditionsMapper;

    @Mock
    private SiteTermsAndConditionsMapper siteTermsAndConditionsMapper;

    @Override
    protected TermsAndConditionsServiceImpl supplyServiceUnderTest() {
        return new TermsAndConditionsServiceImpl(grantTermsAndConditionsRepository,
                siteTermsAndConditionsRepository,
                grantTermsAndConditionsMapper,
                siteTermsAndConditionsMapper);
    }

    @Test
    public void test_getTemplateById() {
        String name = "Innovate UK";
        GrantTermsAndConditions termsAndConditions = newGrantTermsAndConditions().withName(name).build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource().withName(name)
                .build();

        when(grantTermsAndConditionsRepository.findById(termsAndConditions.getId())).thenReturn(Optional.of(termsAndConditions));
        when(grantTermsAndConditionsMapper.mapToResource(termsAndConditions)).thenReturn(termsAndConditionsResource);

        ServiceResult<GrantTermsAndConditionsResource> result = service.getById(termsAndConditions.getId());
        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(name, result.getSuccess().getName());
    }

    @Test
    public void test_getTemplateByNull() {
        ServiceResult<GrantTermsAndConditionsResource> result = service.getById(null);
        assertTrue(result.isFailure());
        assertNotNull(result);
    }

    @Test
    public void test_getLatestVersionsForAllTermsAndConditions() {
        List<GrantTermsAndConditions> termsAndConditionsList = newGrantTermsAndConditions().build(3);
        List<GrantTermsAndConditionsResource> termsAndConditionsResourceList = newGrantTermsAndConditionsResource()
                .build(3);

        when(grantTermsAndConditionsRepository.findLatestVersions()).thenReturn(termsAndConditionsList);
        when(grantTermsAndConditionsMapper.mapToResource(termsAndConditionsList)).thenReturn
                (termsAndConditionsResourceList);

        ServiceResult<List<GrantTermsAndConditionsResource>> result = service
                .getLatestVersionsForAllTermsAndConditions();
        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(3, result.getSuccess().size());

    }

    @Test
    public void test_getLatestSiteTermsAndConditions() {
        SiteTermsAndConditions siteTermsAndConditions = newSiteTermsAndConditions().build();
        SiteTermsAndConditionsResource siteTermsAndConditionsResource = newSiteTermsAndConditionsResource().build();

        when(siteTermsAndConditionsRepository.findTopByOrderByVersionDesc()).thenReturn(siteTermsAndConditions);
        when(siteTermsAndConditionsMapper.mapToResource(siteTermsAndConditions)).thenReturn
                (siteTermsAndConditionsResource);

        assertEquals(siteTermsAndConditionsResource, service.getLatestSiteTermsAndConditions().getSuccess());

        InOrder inOrder = inOrder(siteTermsAndConditionsRepository, siteTermsAndConditionsMapper);
        inOrder.verify(siteTermsAndConditionsRepository).findTopByOrderByVersionDesc();
        inOrder.verify(siteTermsAndConditionsMapper).mapToResource(siteTermsAndConditions);
    }
}
