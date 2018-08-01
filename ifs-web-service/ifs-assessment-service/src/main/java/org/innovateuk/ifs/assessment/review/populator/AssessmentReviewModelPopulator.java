package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewViewModel;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;

/**
 * Build the model for the Assessment Assignment view.
 */
@Component
public class AssessmentReviewModelPopulator {

    private UserRestService userRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private ReviewRestService reviewRestService;
    private OrganisationService organisationService;

    public AssessmentReviewModelPopulator(UserRestService userRestService,
                                          FormInputResponseRestService formInputResponseRestService,
                                          ReviewRestService reviewRestService,
                                          OrganisationService organisationService) {
        this.organisationService = organisationService;
        this.userRestService = userRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.reviewRestService = reviewRestService;
    }

    public AssessmentReviewViewModel populateModel(long reviewId) {
        ReviewResource reviewResource =
                reviewRestService.getAssessmentReview(reviewId).getSuccess();

        String projectSummary = getProjectSummary(reviewResource);
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(reviewResource.getApplication()).getSuccess();
        SortedSet<OrganisationResource> collaborators = organisationService.getApplicationOrganisations(processRoles);
        OrganisationResource leadPartner = organisationService.getApplicationLeadOrganisation(processRoles).orElse(null);

        return new AssessmentReviewViewModel(
                reviewId,
                reviewResource.getCompetition(),
                reviewResource.getApplicationName(),
                collaborators,
                leadPartner,
                projectSummary);
    }

    private String getProjectSummary(ReviewResource reviewResource) {
        Optional<FormInputResponseResource> formInputResponseResource = formInputResponseRestService.getByApplicationIdAndQuestionSetupType(
                reviewResource.getApplication(), PROJECT_SUMMARY).getOptionalSuccessObject();

        if(formInputResponseResource.isPresent()) {
            return formInputResponseResource.get().getValue();
        }
        return null;
    }
}