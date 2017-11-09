package org.innovateuk.ifs.security;

import org.innovateuk.ifs.address.service.AddressRestServiceImpl;
import org.innovateuk.ifs.affiliation.service.AffiliationRestServiceImpl;
import org.innovateuk.ifs.alert.service.AlertRestServiceImpl;
import org.innovateuk.ifs.applicant.service.ApplicantRestServiceImpl;
import org.innovateuk.ifs.application.finance.view.UnsavedFieldsManager;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.assessment.service.*;
import org.innovateuk.ifs.benchmark.BenchmarkController;
import org.innovateuk.ifs.category.service.CategoryRestServiceImpl;
import org.innovateuk.ifs.commons.AbstractServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.competition.service.*;
import org.innovateuk.ifs.exception.IfsErrorController;
import org.innovateuk.ifs.file.service.FileEntryRestServiceImpl;
import org.innovateuk.ifs.filter.ConnectionCountFilter;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.service.*;
import org.innovateuk.ifs.form.service.FormInputResponseRestServiceImpl;
import org.innovateuk.ifs.form.service.FormInputRestServiceImpl;
import org.innovateuk.ifs.form.service.FormValidatorRestServiceImpl;
import org.innovateuk.ifs.invite.service.*;
import org.innovateuk.ifs.metrics.ConnectionCountService;
import org.innovateuk.ifs.organisation.service.CompanyHouseRestServiceImpl;
import org.innovateuk.ifs.organisation.service.OrganisationAddressRestServiceImpl;
import org.innovateuk.ifs.profile.service.ProfileRestServiceImpl;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestServiceImpl;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestServiceImpl;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestServiceImpl;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestServiceImpl;
import org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerRestServiceImpl;
import org.innovateuk.ifs.project.otherdocuments.service.OtherDocumentsRestServiceImpl;
import org.innovateuk.ifs.project.projectdetails.service.ProjectDetailsRestServiceImpl;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestServiceImpl;
import org.innovateuk.ifs.project.service.ProjectRestServiceImpl;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileRestServiceImpl;
import org.innovateuk.ifs.project.status.service.StatusRestServiceImpl;
import org.innovateuk.ifs.publiccontent.service.ContentGroupRestServiceImpl;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.innovateuk.ifs.publiccontent.service.PublicContentRestServiceImpl;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.innovateuk.ifs.upload.service.ProjectFinancePostAttachmentRestService;
import org.innovateuk.ifs.user.service.*;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

public class ServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<? extends Annotation>> classLevelSecurityAnnotations() {
        return  asList(
                PreAuthorize.class,
                PostAuthorize.class
        );
    }

    @Override
    protected List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return asList(Service.class, Controller.class);
    }

    @Override
    protected List<Class<? extends Annotation>> methodLevelSecurityAnnotations() {
        return asList(
                PreAuthorize.class,
                PreFilter.class,
                PostAuthorize.class,
                PostFilter.class,
                NotSecured.class
        );
    }

    @Override
    protected List<Class<?>> excludedClasses() {
        List<Class<?>> filters = asList(
                ConnectionCountFilter.class,
                CookieFlashMessageFilter.class,
                CsrfStatelessFilter.class,
                StatelessAuthenticationFilter.class,
                IfsErrorController.class);
        List<Class<?>> security = asList(
                UidAuthenticationService.class,
                CsrfTokenService.class);
        List<Class<?>> utils = asList(
                CookieUtil.class,
                UnsavedFieldsManager.class,
                ConnectionCountService.class,
                BenchmarkController.class);
        List<Class<?>> exportedRestClasses = asList(ApplicationFinanceRestServiceImpl.class,
                FinanceRowRestServiceImpl.class,
                OrganisationDetailsRestServiceImpl.class,
                ProjectFinanceRowRestServiceImpl.class,
                FinanceRowMetaValueRestServiceImpl.class,
                OverheadFileRestServiceImpl.class,
                FinanceRowMetaFieldRestServiceImpl.class,
                CompanyHouseRestServiceImpl.class,
                OrganisationAddressRestServiceImpl.class,
                AddressRestServiceImpl.class,
                ProjectDetailsRestServiceImpl.class,
                StatusRestServiceImpl.class,
                FinanceCheckRestServiceImpl.class,
                ProjectFinanceQueriesRestService.class,
                ProjectFinanceRestServiceImpl.class,
                ProjectFinanceNotesRestService.class,
                GrantOfferLetterRestServiceImpl.class,
                OtherDocumentsRestServiceImpl.class,
                SpendProfileRestServiceImpl.class,
                BankDetailsRestServiceImpl.class,
                PartnerOrganisationRestServiceImpl.class,
                ProjectRestServiceImpl.class,
                MonitoringOfficerRestServiceImpl.class,
                MilestoneRestServiceImpl.class,
                CompetitionKeyStatisticsRestServiceImpl.class,
                CompetitionRestServiceImpl.class,
                CompetitionPostSubmissionRestServiceImpl.class,
                AssessorCountOptionsRestServiceImpl.class,
                CompetitionSetupRestServiceImpl.class,
                CompetitionSetupFinanceRestServiceImpl.class,
                CompetitionSetupQuestionRestServiceImpl.class,
                ContentGroupRestServiceImpl.class,
                PublicContentRestServiceImpl.class,
                PublicContentItemRestServiceImpl.class,
                QuestionStatusRestServiceImpl.class,
                SectionRestServiceImpl.class,
                AssessorCountSummaryRestServiceImpl.class,
                ApplicationSummaryRestServiceImpl.class,
                QuestionRestServiceImpl.class,
                ApplicationInnovationAreaRestServiceImpl.class,
                ApplicationCountSummaryRestServiceImpl.class,
                ApplicationFundingDecisionRestServiceImpl.class,
                ApplicationResearchCategoryRestServiceImpl.class,
                ApplicationAssessmentSummaryRestServiceImpl.class,
                QuestionSetupRestServiceImpl.class,
                ApplicationRestServiceImpl.class,
                CategoryRestServiceImpl.class,
                AgreementRestServiceImpl.class,
                OrganisationRestServiceImpl.class,
                OrganisationTypeRestServiceImpl.class,
                OrganisationSearchRestServiceImpl.class,
                UserRestServiceImpl.class,
                AffiliationRestServiceImpl.class,
                AssessorCompetitionSummaryRestServiceImpl.class,
                AssessorFormInputResponseRestServiceImpl.class,
                CompetitionParticipantRestServiceImpl.class,
                AssessmentPanelInviteRestServiceImpl.class,
                AssessmentRestServiceImpl.class,
                AssessorRestServiceImpl.class,
                CompetitionInviteRestServiceImpl.class,
                ProfileRestServiceImpl.class,
                InviteRestServiceImpl.class,
                EthnicityRestServiceImpl.class,
                RejectionReasonRestServiceImpl.class,
                ProjectInviteRestServiceImpl.class,
                InviteUserRestServiceImpl.class,
                InviteOrganisationRestServiceImpl.class,
                FileEntryRestServiceImpl.class,
                FormInputRestServiceImpl.class,
                FormValidatorRestServiceImpl.class,
                FormInputResponseRestServiceImpl.class,
                ProjectFinancePostAttachmentRestService.class,
                ApplicantRestServiceImpl.class,
                AlertRestServiceImpl.class);
        return  combineLists(filters, security, utils, exportedRestClasses);
    }
}
