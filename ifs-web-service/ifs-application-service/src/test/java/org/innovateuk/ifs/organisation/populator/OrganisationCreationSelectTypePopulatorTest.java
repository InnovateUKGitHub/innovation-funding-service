package org.innovateuk.ifs.organisation.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class OrganisationCreationSelectTypePopulatorTest extends BaseServiceUnitTest<OrganisationCreationSelectTypePopulator> {

    private final FundingType fundingType;

    @Mock
    protected RegistrationCookieService registrationCookieService;

    @Mock
    protected OrganisationTypeRestService organisationTypeRestService;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public OrganisationCreationSelectTypePopulatorTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Override
    protected OrganisationCreationSelectTypePopulator supplyServiceUnderTest() {
        return new OrganisationCreationSelectTypePopulator();
    }

    @Test
    public void populate() {

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .build();

        List<OrganisationTypeResource> expectedOrganisationTypes = newOrganisationTypeResource()
                .withId(1L, 4L)
                .withName("Business", "Public sector charity or non Je-S registered research organisation")
                .build(2);

        List<OrganisationTypeResource> organisationTypes = newOrganisationTypeResource()
                .withId(1L, 2L, 3L, 4L, 5L)
                .withName("Business", "Research", "Research and technology organisation (RTO)",
                        "Public sector charity or non Je-S registered research organisation", "Knowledge base")
                .build(5);

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));
        when(registrationCookieService.isInternationalJourney(any(HttpServletRequest.class))).thenReturn(false);
        when(registrationCookieService.isLeadJourney(any(HttpServletRequest.class))).thenReturn(true);

        OrganisationCreationSelectTypeViewModel viewModel = service.populate(null, competition);

        assertNotNull(viewModel);
        assertEquals(2, viewModel.getTypes().size());
        assertEquals("Business", viewModel.getTypes().get(0).getName());
        assertEquals("Public sector charity or non Je-S registered research organisation"
                , viewModel.getTypes().get(1).getName());
    }
}
