package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.SicCode;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void organisationTypeMatches_TestDates() throws Exception {
        assertTrue(service.matchLocalDate(LocalDate.of(2020,1,26), LocalDate.of(2020,1,26)));
        assertFalse(service.matchLocalDate(LocalDate.of(2020,1,25), LocalDate.of(2020,1,26)));
    }

    @Test
    public void organisationTypeMatches_TestExecutiveOfficers() throws Exception {

        final  Organisation organisation = getOrganisationWithExecs();
        final OrganisationResource organisationResource = getOrganisationResourceWithExecs();

        assertTrue(service.executiveOfficersMatch(organisationResource,organisation));

        organisationResource.getExecutiveOfficers().get(0).setName("mango");
        assertFalse(service.executiveOfficersMatch(organisationResource, organisation));
    }

    @Test
    public void organisationTypeMatches_TestSicCodes() throws Exception {

        final  Organisation organisation = getOrganisationWithSicCodes();
        final OrganisationResource organisationResource = getOrganisationResourceWithSicCodes();

        assertTrue(service.sicCodesMatch(organisationResource,organisation));

        organisationResource.getSicCodes().get(0).setSicCode("mango");
        assertFalse(service.sicCodesMatch(organisationResource, organisation));
    }

    private Organisation getOrganisationWithExecs() {
        Organisation organisation = newOrganisation().withId(1L).build();
        List<ExecutiveOfficer> officer = new ArrayList<>();
        ExecutiveOfficer officer1 = new ExecutiveOfficer();
        officer1.setOrganisation(organisation);
        officer1.setName("james");
        officer1.setId(1L);
        officer.add(officer1);
        ExecutiveOfficer officer2 = new ExecutiveOfficer();
        officer1.setOrganisation(organisation);
        officer1.setName("james1");
        officer1.setId(1L);
        officer.add(officer2);
        organisation.setExecutiveOfficers(officer);
        return organisation;
    }

    private OrganisationResource getOrganisationResourceWithExecs(){

        OrganisationResource organisationResource  = newOrganisationResource().build();
        List<OrganisationExecutiveOfficerResource> officerResourceArrayList = new ArrayList<>();
        OrganisationExecutiveOfficerResource officerResource = new OrganisationExecutiveOfficerResource();
        officerResource.setOrganisation(1L);
        officerResource.setName("james");
        officerResource.setId(1L);

        OrganisationExecutiveOfficerResource officerResource2 = new OrganisationExecutiveOfficerResource();
        officerResource.setOrganisation(1L);
        officerResource.setName("james1");
        officerResource.setId(1L);

        officerResourceArrayList.add(officerResource2);
        officerResourceArrayList.add(officerResource);
        organisationResource.setExecutiveOfficers(officerResourceArrayList);
        return organisationResource;
    }

    private Organisation getOrganisationWithSicCodes() {
        Organisation organisation = newOrganisation().withId(1L).build();
        List<SicCode> sicCodes = new ArrayList<>();
        SicCode sicCode1 = new SicCode();
        sicCode1.setOrganisation(organisation);
        sicCode1.setSicCode("12345");
        sicCode1.setId(1L);
        sicCodes.add(sicCode1);
        SicCode sicCode2 = new SicCode();
        sicCode2.setOrganisation(organisation);
        sicCode2.setSicCode("54321");
        sicCode2.setId(1L);
        sicCodes.add(sicCode2);
        organisation.setSicCodes(sicCodes);
        return organisation;
    }

    private OrganisationResource getOrganisationResourceWithSicCodes(){

        OrganisationResource organisationResource  = newOrganisationResource().build();
        List<OrganisationSicCodeResource> sicCodes = new ArrayList<>();
        OrganisationSicCodeResource sicCodeResource = new OrganisationSicCodeResource();
        sicCodeResource.setOrganisation(1L);
        sicCodeResource.setSicCode("54321");
        sicCodeResource.setId(1L);

        OrganisationSicCodeResource sicCodeResource2 = new OrganisationSicCodeResource();
        sicCodeResource2.setOrganisation(1L);
        sicCodeResource2.setSicCode("12345");
        sicCodeResource2.setId(1L);

        sicCodes.add(sicCodeResource);
        sicCodes.add(sicCodeResource2);
        organisationResource.setSicCodes(sicCodes);
        return organisationResource;
    }
}