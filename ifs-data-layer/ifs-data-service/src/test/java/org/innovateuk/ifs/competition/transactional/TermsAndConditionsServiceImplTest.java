package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competition.domain.TermsAndConditions;
import org.innovateuk.ifs.competition.mapper.SiteTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.mapper.TermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.SiteTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.TermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder
        .newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsBuilder.newTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class TermsAndConditionsServiceImplTest extends BaseServiceUnitTest<TermsAndConditionsServiceImpl> {

    @Mock
    private SiteTermsAndConditionsRepository siteTermsAndConditionsRepository;

    @Mock
    private SiteTermsAndConditionsMapper siteTermsAndConditionsMapper;

    @Override
    protected TermsAndConditionsServiceImpl supplyServiceUnderTest() {
        return new TermsAndConditionsServiceImpl(siteTermsAndConditionsRepository, siteTermsAndConditionsMapper);
    }

    @Test
    public void test_getTemplateById() {
        String name = "Innovate UK";
        TermsAndConditions termsAndConditions = newTermsAndConditions().withName(name).build();
        TermsAndConditionsResource termsAndConditionsResource = newTermsAndConditionsResource().withName(name).build();

        when(termsAndConditionsRepository.findOne(termsAndConditions.getId())).thenReturn(termsAndConditions);
        when(termsAndConditionsMapper.mapToResource(termsAndConditions)).thenReturn(termsAndConditionsResource);

        ServiceResult<TermsAndConditionsResource> result = service.getById(termsAndConditions.getId());
        assertTrue(result.isSuccess());
        assertNotNull(result);
        assertEquals(name, result.getSuccess().getName());
    }

    @Test
    public void test_getTemplateByNull() {
        ServiceResult<TermsAndConditionsResource> result = service.getById(null);
        assertTrue(result.isFailure());
        assertNotNull(result);
    }

    @Test
    public void test_getLatestVersionsForAllTermsAndConditions() {
        List<TermsAndConditions> termsAndConditionsList = newTermsAndConditions().build(3);
        List<TermsAndConditionsResource> termsAndConditionsResourceList = newTermsAndConditionsResource().build(3);

        when(termsAndConditionsRepository.findLatestVersions()).thenReturn(termsAndConditionsList);
        when(termsAndConditionsMapper.mapToResource(termsAndConditionsList)).thenReturn(termsAndConditionsResourceList);

        ServiceResult<List<TermsAndConditionsResource>> result = service.getLatestVersionsForAllTermsAndConditions();
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
