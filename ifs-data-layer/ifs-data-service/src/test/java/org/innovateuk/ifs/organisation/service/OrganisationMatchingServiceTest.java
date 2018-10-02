package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationMatchingServiceTest extends BaseServiceUnitTest<OrganisationMatchingServiceImpl> {

    @Mock
    private OrganisationPatternMatcher organisationPatternMatcher;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Override
    protected OrganisationMatchingServiceImpl supplyServiceUnderTest() {
        return new OrganisationMatchingServiceImpl();
    }

    private String academicName;
    private Organisation matchingResearchOrganisation;
    private OrganisationResource submittedResearchOrganisation;

    private String companiesHouseNumber;
    private Organisation matchingBusinessOrganisation;
    private OrganisationResource submittedBusinessOrganisation;

    @Before
    public void setUp() {
        companiesHouseNumber = "1234";
        matchingBusinessOrganisation = newOrganisation()
                .withCompaniesHouseNumber(companiesHouseNumber)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        submittedBusinessOrganisation = newOrganisationResource()
                .withCompaniesHouseNumber(companiesHouseNumber)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        academicName = "academic";
        matchingResearchOrganisation = newOrganisation()
                .withName(academicName)
                .withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        submittedResearchOrganisation = newOrganisationResource()
                .withName(academicName)
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldMatchByNameAndCallsPatternMatchers() throws Exception {
        when(organisationRepositoryMock.findByNameOrderById(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertTrue(result.isPresent());

        verify(organisationPatternMatcher, times(0)).organisationTypeMatches(any(), any());
        verify(organisationPatternMatcher, times(1)).organisationTypeIsResearch(any());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldNotMatchWhenTypeIsNotResearch() throws Exception {
        when(organisationRepositoryMock.findByNameOrderById(eq(academicName))).thenReturn(Arrays.asList(matchingResearchOrganisation));
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_academicOrganisationShouldWhenNoOrganisationWithNameIsFound() throws Exception {
        when(organisationRepositoryMock.findByNameOrderById(eq(academicName))).thenReturn(Collections.emptyList());
        when(organisationPatternMatcher.organisationTypeIsResearch(any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedResearchOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldMatchWhenCompaniesHouseNumberAndTypeMatchAndCallsPatternMatchers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumberOrderById(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertTrue(result.isPresent());

        verify(organisationPatternMatcher, times(1)).organisationTypeMatches(any(), any());
        verify(organisationPatternMatcher, times(0)).organisationTypeIsResearch(any());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchWhenOrganisationTypeDiffers() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumberOrderById(eq(companiesHouseNumber))).thenReturn(Arrays.asList(matchingBusinessOrganisation));
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(false);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }

    @Test
    public void findOrganisationMatch_companiesHouseOrganisationShouldNotMatchNoMatchingOrganisationIsFound() throws Exception {
        when(organisationRepositoryMock.findByCompanyHouseNumberOrderById(eq(companiesHouseNumber))).thenReturn(Collections.emptyList());
        when(organisationPatternMatcher.organisationTypeMatches(any(), any())).thenReturn(true);

        Optional<Organisation> result = service.findOrganisationMatch(submittedBusinessOrganisation);

        assertFalse(result.isPresent());
    }
}