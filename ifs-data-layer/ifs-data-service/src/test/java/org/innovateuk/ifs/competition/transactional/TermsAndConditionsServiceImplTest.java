package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competition.mapper.SiteTermsAndConditionsMapper;
import org.innovateuk.ifs.competition.repository.SiteTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder
        .newSiteTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
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
    public void getLatestSiteTermsAndConditions() {
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