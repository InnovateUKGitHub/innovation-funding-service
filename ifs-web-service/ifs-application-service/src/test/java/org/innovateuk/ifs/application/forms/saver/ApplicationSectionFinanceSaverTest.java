package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ApplicationSectionFinanceSaver}
 */

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSectionFinanceSaverTest {

    @InjectMocks
    private ApplicationSectionFinanceSaver saver;

    @Mock
    private SectionService sectionService;

    private final Long competitionId = 5L;

    @Before
    public void setup() {
        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES))
                .thenReturn(
                        newSectionResource()
                                .withCompetition(competitionId)
                                .withType(SectionType.ORGANISATION_FINANCES)
                                .build(1));

        when(sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES))
                .thenReturn(
                        newSectionResource()
                                .withCompetition(competitionId)
                                .withType(SectionType.FUNDING_FINANCES)
                                .build(1));
    }

    @Test
    public void handleMarkAcademicFinancesAsNotRequired() {
        saver.handleMarkAcademicFinancesAsNotRequired(OrganisationTypeEnum.PUBLICSECTOR_OR_CHARITY.getId(), newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build(), 3L, competitionId, 7L);
        saver.handleMarkAcademicFinancesAsNotRequired(OrganisationTypeEnum.RESEARCH.getId(), newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build(), 3L, competitionId, 7L);

        saver.handleMarkAcademicFinancesAsNotRequired(OrganisationTypeEnum.RESEARCH.getId(), newSectionResource().withType(SectionType.PROJECT_COST_FINANCES).build(), 3L, competitionId, 7L);

        verify(sectionService, times(2)).markAsNotRequired(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void handleStateAid_MarkAsComplete() {
        final Map<String, String[]> params = new HashMap<>();
        final ApplicationResource application = newApplicationResource().with(applicationResource -> applicationResource.setStateAidAgreed(null)).build();
        final ApplicationForm form = new ApplicationForm();
        form.setStateAidAgreed(Boolean.TRUE);
        final SectionResource selectedSection = newSectionResource().withType(FINANCE).build();

        saver.handleStateAid(params, application, form, selectedSection);
        assertEquals(null, application.getStateAidAgreed());

        params.put(MARK_SECTION_AS_COMPLETE, null);
        saver.handleStateAid(params, application, form, selectedSection);
        assertEquals(Boolean.TRUE, application.getStateAidAgreed());
    }

    @Test
    public void handleStateAid_MarkAsInComplete() {
        final Map<String, String[]> params = new HashMap<>();
        final ApplicationResource application = newApplicationResource().with(applicationResource -> applicationResource.setStateAidAgreed(null)).build();
        final ApplicationForm form = new ApplicationForm();
        form.setStateAidAgreed(Boolean.TRUE);
        final SectionResource selectedSection = newSectionResource().withType(FINANCE).build();

        params.put(MARK_SECTION_AS_INCOMPLETE, null);
        saver.handleStateAid(params, application, form, selectedSection);

        assertEquals(Boolean.FALSE, application.getStateAidAgreed());
    }

    @Test
    public void handleRequestFundingRequests_requestFunding() {
        final Map<String, String[]> params = new HashMap<>();
        final Long applicationId = 3L;
        final Long processRoleId = 15L;

        params.put(REQUESTING_FUNDING, null);

        saver.handleRequestFundingRequests(params, applicationId, competitionId, processRoleId);

        verify(sectionService, times(2)).markAsInComplete(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void handleRequestFundingRequests_notRequestFunding() {
        final Map<String, String[]> params = new HashMap<>();
        final Long applicationId = 3L;
        final Long processRoleId = 15L;

        params.put(NOT_REQUESTING_FUNDING, null);

        saver.handleRequestFundingRequests(params, applicationId, competitionId, processRoleId);

        verify(sectionService, times(2)).markAsNotRequired(anyLong(), anyLong(), anyLong());
    }
}
