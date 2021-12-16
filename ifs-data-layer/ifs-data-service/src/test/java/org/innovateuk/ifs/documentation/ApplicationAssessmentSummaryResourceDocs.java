package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;

public class ApplicationAssessmentSummaryResourceDocs {

    public static final ApplicationAssessmentSummaryResourceBuilder applicationAssessmentSummaryResourceBuilder = newApplicationAssessmentSummaryResource()
            .withId(1L)
            .withName("Progressive machines")
            .withCompetitionId(2L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withLeadOrganisation("Liquid Dynamics")
            .withCompetitionStatus(OPEN)
            .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"));

}