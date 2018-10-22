package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;

import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganisationPatternMatcherTest extends BaseServiceUnitTest<OrganisationPatternMatcher> {

    @Override
    protected OrganisationPatternMatcher supplyServiceUnderTest() {
        return new OrganisationPatternMatcher();
    }

    @Test
    public void organisationTypeIsResearch() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertTrue(result);
    }

    @Test
    public void organisationTypeIsResearch_noMatchIfTypeIsNotResearch() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertFalse(result);
    }

    @Test
    public void organisationTypeIsResearch_noMatchIfTypeIsMissing() throws Exception {
        Organisation organisation = newOrganisation().build();
        boolean result = service.organisationTypeIsResearch(organisation);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertTrue(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenTypesDiffer() throws Exception {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.RTO).build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenOrganisationMissesType() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource  = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }

    @Test
    public void organisationTypeMatches_noMatchWhenOrganisationResourceIsNull() throws Exception {
        Organisation organisation = newOrganisation().build();
        OrganisationResource organisationResource  = null;

        boolean result = service.organisationTypeMatches(organisation, organisationResource);
        assertFalse(result);
    }
}