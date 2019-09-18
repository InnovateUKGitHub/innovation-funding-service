package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.resource.search.*;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A utility for commonly used ParameterizedTypeReferences
 */
public final class ParameterizedTypeReferences {

    private ParameterizedTypeReferences() {}

    /**
     * Basic types
     */

    public static ParameterizedTypeReference<List<Long>> longsListType() {
        return new ParameterizedTypeReference<List<Long>>() {};
    }

    public static ParameterizedTypeReference<Set<Long>> longsSetType() {
        return new ParameterizedTypeReference<Set<Long>>() {};
    }

    public static ParameterizedTypeReference<List<String>> stringsListType() {
        return new ParameterizedTypeReference<List<String>>() {};
    }

    public static ParameterizedTypeReference<Map<Long, Set<Long>>> mapOfLongToLongsSetType() {
        return new ParameterizedTypeReference<Map<Long, Set<Long>>>() {};
    }

    /**
     * IFS types
     */

    public static ParameterizedTypeReference<List<AffiliationResource>> affiliationResourceListType() {
        return new ParameterizedTypeReference<List<AffiliationResource>>() {};
    }

    public static ParameterizedTypeReference<AffiliationListResource> affiliationListResourceType() {
        return new ParameterizedTypeReference<AffiliationListResource>() {};
    }

    public static ParameterizedTypeReference<List<AlertResource>> alertResourceListType() {
        return new ParameterizedTypeReference<List<AlertResource>>() {};
    }

    public static ParameterizedTypeReference<List<ApplicationResource>> applicationResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationResource>>() {};
    }

    public static ParameterizedTypeReference<List<ApplicationAssessorResource>> applicationAssessorResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationAssessorResource>>() {};
    }

    public static ParameterizedTypeReference<List<AssessorFormInputResponseResource>> assessorFormInputResponseResourceListType() {
        return new ParameterizedTypeReference<List<AssessorFormInputResponseResource>>() {};
    }

    public static ParameterizedTypeReference<List<AvailableAssessorResource>> availableAssessorResourceListType() {
        return new ParameterizedTypeReference<List<AvailableAssessorResource>>() {};
    }

    public static ParameterizedTypeReference<List<OrganisationSearchResult>> organisationSearchResultListType() {
        return new ParameterizedTypeReference<List<OrganisationSearchResult>>() {
        };
    }

    public static ParameterizedTypeReference<List<ProcessRoleResource>> processRoleResourceListType() {
        return new ParameterizedTypeReference<List<ProcessRoleResource>>() {};
    }

    public static ParameterizedTypeReference<List<Role>> roleListType() {
        return new ParameterizedTypeReference<List<Role>>() {};
    }

    public static ParameterizedTypeReference<List<UserResource>> userListType() {
        return new ParameterizedTypeReference<List<UserResource>>() {};
    }

    public static ParameterizedTypeReference<List<SimpleUserResource>> simpleUserListType() {
        return new ParameterizedTypeReference<List<SimpleUserResource>>() {};
    }

    public static ParameterizedTypeReference<List<UserOrganisationResource>> userOrganisationListType() {
        return new ParameterizedTypeReference<List<UserOrganisationResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionResource>> competitionResourceListType() {
        return new ParameterizedTypeReference<List<CompetitionResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionSearchResultItem>> competitionSearchResultItemListType() {
        return new ParameterizedTypeReference<List<CompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<LiveCompetitionSearchResultItem>> liveCompetitionSearchResultItemListType() {
        return new ParameterizedTypeReference<List<LiveCompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<UpcomingCompetitionSearchResultItem>> upcomingCompetitionSearchResultItemListType() {
        return new ParameterizedTypeReference<List<UpcomingCompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<NonIfsCompetitionSearchResultItem>> nonIfsCompetitionSearchReultItemListType() {
        return new ParameterizedTypeReference<List<NonIfsCompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<ProjectSetupCompetitionSearchResultItem>> projectSetupCompetitionSearchResultItemListType() {
        return new ParameterizedTypeReference<List<ProjectSetupCompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<PreviousCompetitionSearchResultItem>> previousCompetitionSearchResultItemListType() {
        return new ParameterizedTypeReference<List<PreviousCompetitionSearchResultItem>>() {};
    }

    public static ParameterizedTypeReference<List<PreviousApplicationResource>> previousApplicationResourceListType() {
        return new ParameterizedTypeReference<List<PreviousApplicationResource>>() {};
    }

    public static ParameterizedTypeReference<List<InnovationAreaResource>> innovationAreaResourceListType() {
        return new ParameterizedTypeReference<List<InnovationAreaResource>>() {};
    }

    public static ParameterizedTypeReference<List<InnovationSectorResource>> innovationSectorResourceListType() {
        return new ParameterizedTypeReference<List<InnovationSectorResource>>() {};
    }

    public static ParameterizedTypeReference<List<ResearchCategoryResource>> researchCategoryResourceListType() {
        return new ParameterizedTypeReference<List<ResearchCategoryResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionTypeResource>> competitionTypeResourceListType() {
        return new ParameterizedTypeReference<List<CompetitionTypeResource>>() {};
    }

    public static ParameterizedTypeReference<List<QuestionStatusResource>> questionStatusResourceListType() {
        return new ParameterizedTypeReference<List<QuestionStatusResource>>() {};
    }

    public static ParameterizedTypeReference<List<FormInputResource>> formInputResourceListType() {
        return new ParameterizedTypeReference<List<FormInputResource>>() {};
    }

    public static ParameterizedTypeReference<List<QuestionResource>> questionResourceListType() {
        return new ParameterizedTypeReference<List<QuestionResource>>() {};
    }

    public static ParameterizedTypeReference<List<FormInputResponseResource>> formInputResponseListType() {
        return new ParameterizedTypeReference<List<FormInputResponseResource>>() {};
    }

    public static ParameterizedTypeReference<List<FinanceRowMetaFieldResource>> financeRowMetaFieldResourceListType() {
        return new ParameterizedTypeReference<List<FinanceRowMetaFieldResource>>() {};
    }

    public static ParameterizedTypeReference<List<InviteOrganisationResource>> inviteOrganisationResourceListType() {
        return new ParameterizedTypeReference<List<InviteOrganisationResource>>() {};
    }

    public static ParameterizedTypeReference<List<ProjectUserInviteResource>> projectInviteResourceListType() {
        return new ParameterizedTypeReference<List<ProjectUserInviteResource>>() {};
    }

    public static ParameterizedTypeReference<List<ExternalInviteResource>> externalInviteResourceListType() {
        return new ParameterizedTypeReference<List<ExternalInviteResource>>() {};
    }

    public static ParameterizedTypeReference<List<FinanceRowItem>> costItemListType() {
        return new ParameterizedTypeReference<List<FinanceRowItem>>() {};
    }

    public static ParameterizedTypeReference<List<ApplicationFinanceResource>> applicationFinanceResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationFinanceResource>>() {};
    }

    public static ParameterizedTypeReference<List<ProjectFinanceResource>> projectFinanceResourceListType() {
        return new ParameterizedTypeReference<List<ProjectFinanceResource>>() {};
    }

    public static ParameterizedTypeReference<List<QueryResource>> queryResourceListType() {
        return new ParameterizedTypeReference<List<QueryResource>>() {};
    }

    public static ParameterizedTypeReference<List<NoteResource>> noteResourceListType() {
        return new ParameterizedTypeReference<List<NoteResource>>() {};
    }

    public static ParameterizedTypeReference<List<OrganisationTypeResource>> organisationTypeResourceListType() {
        return new ParameterizedTypeReference<List<OrganisationTypeResource>>() {};
    }

    public static ParameterizedTypeReference<List<AddressResource>> addressResourceListType() {
        return new ParameterizedTypeReference<List<AddressResource>>() {};
    }

    public static ParameterizedTypeReference<List<OrganisationResource>> organisationResourceListType() {
        return new ParameterizedTypeReference<List<OrganisationResource>>() {};
    }

    public static ParameterizedTypeReference<List<ProjectResource>> projectResourceListType() {
        return new ParameterizedTypeReference<List<ProjectResource>>() {
        };
    }

    public static ParameterizedTypeReference<List<ProjectStatusResource>> projectStatusResourceListType() {
        return new ParameterizedTypeReference<List<ProjectStatusResource>>() {
        };
    }

    public static ParameterizedTypeReference<List<ProjectUserResource>> projectUserResourceList() {
        return new ParameterizedTypeReference<List<ProjectUserResource>>() {
        };
    }

    public static ParameterizedTypeReference<List<PartnerOrganisationResource>> partnerOrganisationResourceList() {
        return new ParameterizedTypeReference<List<PartnerOrganisationResource>>() {
        };
    }

    public static ParameterizedTypeReference<List<RejectionReasonResource>> rejectionReasonResourceListType() {
        return new ParameterizedTypeReference<List<RejectionReasonResource>>() {
        };
    }

    public static ParameterizedTypeReference<List<ValidationMessages>> validationMessagesListType() {
        return new ParameterizedTypeReference<List<ValidationMessages>>() {};
    }

    public static ParameterizedTypeReference<List<MilestoneResource>> milestoneResourceListType() {
        return new ParameterizedTypeReference<List<MilestoneResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionParticipantResource>> competitionParticipantResourceListType() {
        return new ParameterizedTypeReference<List<CompetitionParticipantResource>>() {};
    }

    public static ParameterizedTypeReference<List<ReviewInviteResource>> assessmentPanelInviteResourceListType() {
        return new ParameterizedTypeReference<List<ReviewInviteResource>>() {};
    }

    public static ParameterizedTypeReference<List<ReviewParticipantResource>> assessmentPanelParticipantResourceListType() {
        return new ParameterizedTypeReference<List<ReviewParticipantResource>>() {};
    }

    public static ParameterizedTypeReference<List<InterviewParticipantResource>> assessmentInterviewPanelParticipantResourceListType() {
        return new ParameterizedTypeReference<List<InterviewParticipantResource>>() {};
    }

    public static ParameterizedTypeReference<List<AssessmentResource>> assessmentResourceListType() {
        return new ParameterizedTypeReference<List<AssessmentResource>>() {};
    }

    public static ParameterizedTypeReference<List<ReviewResource>> assessmentReviewResourceListType() {
        return new ParameterizedTypeReference<List<ReviewResource>>() {};
    }

    public static ParameterizedTypeReference<List<AssessorCountOptionResource>> assessorCountOptionResourceListType() {
        return new ParameterizedTypeReference<List<AssessorCountOptionResource>>() {};
    }

    public static ParameterizedTypeReference<List<SectionResource>> sectionResourceListType() {
        return new ParameterizedTypeReference<List<SectionResource>>() {};
    }

    public static ParameterizedTypeReference<List<ApplicationSummaryResource>> applicationSummaryResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationSummaryResource>>() {};
    }

    public static ParameterizedTypeReference<List<ApplicationSummaryResource>> competitionSummaryResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationSummaryResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionOpenQueryResource>> competitionOpenQueryResourceListType() {
        return new ParameterizedTypeReference<List<CompetitionOpenQueryResource>>() {};
    }
    
    public static ParameterizedTypeReference<List<CompetitionDocumentResource>> competitionDocumentResourceListType() {
        return new ParameterizedTypeReference<List<CompetitionDocumentResource>>() {};
    }

    public static ParameterizedTypeReference<List<SpendProfileStatusResource>> spendProfileStatusResourceListType() {
        return new ParameterizedTypeReference<List<SpendProfileStatusResource>>() {};
    }

    public static ParameterizedTypeReference<List<BankDetailsReviewResource>> bankDetailsReviewResourceListType() {
        return new ParameterizedTypeReference<List<BankDetailsReviewResource>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionOpenQueryResource>> competitionOpenQueryListType() {
        return new ParameterizedTypeReference<List<CompetitionOpenQueryResource>>() {};
    }

    public static ParameterizedTypeReference<Map<CompetitionSetupSection, Optional<Boolean>>> competitionSetupSectionStatusMap() {
        return new ParameterizedTypeReference<Map<CompetitionSetupSection, Optional<Boolean>>>() {};
    }

    public static ParameterizedTypeReference<Map<CompetitionSetupSubsection, Optional<Boolean>>> competitionSetupSubsectionStatusMap() {
        return new ParameterizedTypeReference<Map<CompetitionSetupSubsection, Optional<Boolean>>>() {};
    }

    public static ParameterizedTypeReference<List<GrantTermsAndConditionsResource>> grantTermsAndConditionsResourceListType() {
        return new ParameterizedTypeReference<List<GrantTermsAndConditionsResource>>() {};
    }

    public static ParameterizedTypeReference<List<InterviewApplicationResource>> interviewApplicationsResourceListType() {
        return new ParameterizedTypeReference<List<InterviewApplicationResource>>() {};
    }

    public static ParameterizedTypeReference<List<InterviewResource>> interviewResourceListType() {
        return new ParameterizedTypeReference<List<InterviewResource>>() {};
    }

    public static ParameterizedTypeReference<Map<Long, Boolean>> longStatusMap() {
        return new ParameterizedTypeReference<Map<Long, Boolean>>() {};
    }

    public static ParameterizedTypeReference<List<CompetitionResearchCategoryLinkResource>> competitionResearchCategoryLinkList() {
        return new ParameterizedTypeReference<List<CompetitionResearchCategoryLinkResource>>() {};
    }

    public static ParameterizedTypeReference<List<EuActionTypeResource>> euActionTypeResourceListType() {
        return new ParameterizedTypeReference<List<EuActionTypeResource>>() {};
    }

    public static ParameterizedTypeReference<List<MonitoringOfficerAssignmentResource>> monitoringOfficerResourceListType() {
        return new ParameterizedTypeReference<List<MonitoringOfficerAssignmentResource>>() {};
    }

    public static ParameterizedTypeReference<List<ActivityLogResource>> activityLogResourceListType() {
        return new ParameterizedTypeReference<List<ActivityLogResource>>() {};
    }
}
