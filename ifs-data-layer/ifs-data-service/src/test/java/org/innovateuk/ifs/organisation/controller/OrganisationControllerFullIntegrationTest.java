package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.mapper.AddressMapperImpl;
import org.innovateuk.ifs.address.mapper.AddressTypeMapperImpl;
import org.innovateuk.ifs.cfg.BaseFullStackIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapperImpl;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapperImpl;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapperImpl;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.innovateuk.ifs.organisation.service.OrganisationPatternMatcher;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationServiceImpl;
import org.innovateuk.ifs.service.CustomDateTimeProvider;
import org.innovateuk.ifs.user.mapper.UserMapperImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = {
        OrganisationController.class,
        OrganisationServiceImpl.class,
        OrganisationMatchingServiceImpl.class,
        AuditingEntityListener.class,
        CustomDateTimeProvider.class,
        OrganisationPatternMatcher.class,
        OrganisationMapperImpl.class,
        OrganisationAddressMapperImpl.class,
        AddressMapperImpl.class,
        AddressTypeMapperImpl.class,
        OrganisationTypeMapperImpl.class,
        UserMapperImpl.class
})
public class OrganisationControllerFullIntegrationTest extends BaseFullStackIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationController organisationController;

    @MockBean
    private OrganisationInitialCreationService organisationInitialCreationService;

    private static String TEST_ORG_NAME_NO_CHN = "Trotters Independent Trading";
    private static String TEST_ORG_NAME_WITH_CHN = "Genco Pura Olive Oil Company";
    private static String UPDATED = " Updated";
    private static String CHN = "123456789";
    private static String CHN_UPDATED = "987654321";

    @Before
    public void initTestData() {
        List<Organisation> orgs = organisationRepository.findByNameOrderById(TEST_ORG_NAME_NO_CHN);
        orgs.forEach(o -> organisationRepository.deleteById(o.getId()));
        orgs = organisationRepository.findByNameOrderById(TEST_ORG_NAME_WITH_CHN);
        orgs.forEach(o -> organisationRepository.deleteById(o.getId()));

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(TEST_ORG_NAME_NO_CHN);
        organisationResource.setOrganisationType(OrganisationTypeEnum.BUSINESS.getId());
        organisationService.create(organisationResource);

        organisationResource = new OrganisationResource();
        organisationResource.setName(TEST_ORG_NAME_WITH_CHN);
        organisationResource.setCompaniesHouseNumber(CHN);
        organisationResource.setOrganisationType(OrganisationTypeEnum.BUSINESS.getId());
        organisationService.create(organisationResource);
    }

    @Test
    public void testUpdateOrganisation() {
        List<OrganisationResource> organisations = organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN).getSuccess();
        assertThat(organisations.size(), equalTo(1));
        assertThat(organisations.get(0).getName(), equalTo(TEST_ORG_NAME_WITH_CHN));
        assertThat(organisations.get(0).getCompaniesHouseNumber(), equalTo(CHN));
        Long id = organisations.get(0).getId();
        organisationController.updateNameAndRegistration(id, TEST_ORG_NAME_WITH_CHN + UPDATED, CHN_UPDATED);

        RestResult<List<OrganisationResource>> organisationResult = organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN);
        assertThat(organisationResult.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

        organisationResult = organisationController.findOrganisationsByName(TEST_ORG_NAME_WITH_CHN + UPDATED);
        assertThat(organisationResult.getSuccess().size(), equalTo(1));
    }
}