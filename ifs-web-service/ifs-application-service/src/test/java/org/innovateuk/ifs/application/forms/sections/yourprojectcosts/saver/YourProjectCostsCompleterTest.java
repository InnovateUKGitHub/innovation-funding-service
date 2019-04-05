package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class YourProjectCostsCompleterTest extends BaseServiceUnitTest<YourProjectCostsCompleter> {

    @Mock
    private SectionService sectionService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected YourProjectCostsCompleter supplyServiceUnderTest() {
        return new YourProjectCostsCompleter();
    }

    @Test
    public void markAsComplete() {
        long sectionId = 1L;
        CompetitionResource competition = newCompetitionResource()
                .withIncludeYourOrganisationSection(false)
                .withFundingType(FundingType.PROCUREMENT)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId())
                .build();
        ProcessRoleResource role = newProcessRoleResource()
                .withApplication(application.getId())
                .withOrganisation(organisation.getId())
                .build();
        SectionResource organisationSection = newSectionResource().build();
        SectionResource fundingSection = newSectionResource().build();

        when(sectionService.markAsComplete(sectionId, application.getId(), role.getId())).thenReturn(emptyList());
        when(applicationRestService.getApplicationById(role.getApplicationId())).thenReturn(restSuccess(application));
        when(organisationRestService.getOrganisationById(role.getOrganisationId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.ORGANISATION_FINANCES)).thenReturn(singletonList(organisationSection));
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.FUNDING_FINANCES)).thenReturn(singletonList(fundingSection));

        service.markAsComplete(sectionId, application.getId(), role);

        verify(sectionService).markAsComplete(sectionId, application.getId(), role.getId());
        verify(sectionService).markAsNotRequired(organisationSection.getId(), application.getId(), role.getId());
        verify(sectionService).markAsNotRequired(fundingSection.getId(), application.getId(), role.getId());
    }
}
