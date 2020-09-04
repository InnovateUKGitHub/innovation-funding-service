package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AutoCompleteSectionsUtilTest {

    @Mock
    private OrganisationService organisationService;

    @Mock
    private SectionStatusService sectionStatusService;

    @InjectMocks
    private AutoCompleteSectionsUtil util;

    @Test
    public void intitialiseCompleteSectionsForOrganisation() {
        SectionType type = mock(SectionType.class);
        Section section = newSection().withSectionType(type).build();
        Competition competition = newCompetition()
                .withSections(newArrayList(section))
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        Application application = newApplication()
                .withCompetition(competition)
                .withProcessRoles(newProcessRole().withRole(Role.LEADAPPLICANT).withOrganisationId(organisation.getId()).build())
                .build();
        long processRoleId = 2L;
        when(type.isSectionTypeNotRequiredForOrganisationAndCompetition(competition, OrganisationTypeEnum.BUSINESS, true)).thenReturn(true);
        when(sectionStatusService.markSectionAsComplete(section.getId(), application.getId(), processRoleId)).thenReturn(serviceSuccess(new ValidationMessages()));

        when(organisationService.findById(organisation.getId())).thenReturn(serviceSuccess(organisation));

        util.intitialiseCompleteSectionsForOrganisation(application, organisation.getId(), processRoleId);

        verify(sectionStatusService).markSectionAsNotRequired(section.getId(), application.getId(), processRoleId);
    }
}
