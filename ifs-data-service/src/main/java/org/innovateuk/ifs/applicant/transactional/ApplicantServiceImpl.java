package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.application.transactional.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service implementation for retrieving rich resources for application sections and questions.
 */
@Service
public class ApplicantServiceImpl extends BaseTransactionalService implements ApplicantService {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseUserService baseUserService;


    @Override
    public ServiceResult<ApplicantQuestionResource> getQuestion(Long userId, Long questionId, Long applicationId) {
        ServiceResults results = new ServiceResults();
        ApplicantQuestionResource applicant = new ApplicantQuestionResource();
        populateAbstractApplicantResource(applicant, applicationId, userId, results);

        populateQuestion(results, applicant, questionId, applicationId, applicant.getApplicants());

        return results.toSingle().andOnSuccessReturn(() -> applicant);
    }

    @Override
    public ServiceResult<ApplicantSectionResource> getSection(Long userId, Long sectionId, Long applicationId) {
        ServiceResults results = new ServiceResults();
        ApplicantSectionResource applicant = new ApplicantSectionResource();
        populateAbstractApplicantResource(applicant, applicationId, userId, results);

        populateSection(results, applicant, sectionId, applicationId, applicant.getApplicants());


        if (results.isSuccessful()) {
            if (applicant.getSection().getParentSection() != null) {
                ApplicantSectionResource parent = new ApplicantSectionResource();
                populateSection(results, parent, applicant.getSection().getParentSection(), applicationId, applicant.getApplicants());
                applicant.setApplicantParentSection(parent);
            }

            applicant.getSection().getChildSections().forEach(subSectionId -> {
                ApplicantSectionResource applicantSectionResource = new ApplicantSectionResource();
                populateSection(results, applicantSectionResource, subSectionId, applicationId, applicant.getApplicants());
                applicant.addChildSection(applicantSectionResource);
            });
        }
        return results.toSingle().andOnSuccessReturn(() -> applicant);
    }


    private void populateQuestion(ServiceResults results, ApplicantQuestionResource applicant, Long questionId, Long applicationId, List<ApplicantResource> applicants) {
        results.trackResult(() -> questionService.getQuestionById(questionId), applicant::setQuestion);
        results.trackResult(() -> questionService.getQuestionStatusByQuestionIdAndApplicationId(questionId, applicationId), questionStatusResources -> applicant.setApplicantQuestionStatuses(mapToApplicantStatuses(questionStatusResources, applicants)));
        results.trackResult(() -> mapFormInputs(results, questionId, applicationId, applicants), applicant::setApplicantFormInputs);
    }

    private List<ApplicantQuestionStatusResource> mapToApplicantStatuses(List<QuestionStatusResource> questionStatusResources, List<ApplicantResource> applicants) {
        return questionStatusResources.stream().map(questionStatusResource -> {
            ApplicantQuestionStatusResource questionStatus = new ApplicantQuestionStatusResource();
            questionStatus.setStatus(questionStatusResource);
            if (questionStatusResource.getAssignee() != null) {
                questionStatus.setAssignee(applicants.stream().filter(applicantResource -> applicantResource.getProcessRole().getId().equals(questionStatusResource.getAssignee())).findAny().orElse(null));
                questionStatus.setAssignedBy(applicants.stream().filter(applicantResource -> applicantResource.getProcessRole().getId().equals(questionStatusResource.getAssignedBy())).findAny().orElse(null));
            }
            if (questionStatusResource.getMarkedAsCompleteBy() != null) {
                questionStatus.setMarkedAsCompleteBy(applicants.stream().filter(applicantResource -> applicantResource.getProcessRole().getId().equals(questionStatusResource.getMarkedAsCompleteBy())).findAny().orElse(null));;
            }
            return questionStatus;
        }).collect(Collectors.toList());
    }

    private void populateSection(ServiceResults results, ApplicantSectionResource applicant, Long sectionId, Long applicationId, List<ApplicantResource> applicants) {
        results.trackResult(() -> sectionService.getById(sectionId), applicant::setSection);

        if (results.isSuccessful()) {
            applicant.getSection().getQuestions().forEach(questionId -> {
                ApplicantQuestionResource applicantQuestionResource = new ApplicantQuestionResource();
                populateQuestion(results, applicantQuestionResource, questionId, applicationId, applicants);
                applicant.addQuestion(applicantQuestionResource);
            });
        }
    }

    private <R extends AbstractApplicantResource> void populateAbstractApplicantResource(R resource, Long applicationId, Long userId, ServiceResults results) {
        mapApplicants(results, resource, applicationId, userId);

        results.trackResult(() -> baseUserService.getUserById(userId), resource::setCurrentUser);
        results.trackResult(() -> applicationService.getApplicationById(applicationId), resource::setApplication);
        results.trackResult(() -> competitionService.getCompetitionById(resource.getApplication().getCompetition()), resource::setCompetition);
    }

    private <R extends AbstractApplicantResource> void mapApplicants(ServiceResults results, R resource, Long applicationId, Long userId) {
        results.trackResult(() -> usersRolesService.getProcessRolesByApplicationId(applicationId), processRoles ->
            processRoles.forEach(processRole -> {
                if (processRole.getOrganisationId() != null) {
                    results.trackResult(() -> toApplicant(results, processRole), applicant -> {
                        if (applicant.getProcessRole().getUser().equals(userId)) {
                            resource.setCurrentApplicant(applicant);
                        }
                        resource.addApplicant(applicant);
                    });
                }
            }));
    }

    private ServiceResult<List<ApplicantFormInputResource>> mapFormInputs(ServiceResults results, Long questionId, Long applicationId, List<ApplicantResource> applicants) {
        List<ApplicantFormInputResource> applicantFormInputResources = new ArrayList<>();
        results.trackResult(() -> formInputService.findByQuestionIdAndScope(questionId, FormInputScope.APPLICATION), formInputResources -> formInputResources.forEach(formInputResource -> {
            ApplicantFormInputResource applicantFormInputResource = new ApplicantFormInputResource();
            applicantFormInputResources.add(applicantFormInputResource);
            applicantFormInputResource.setFormInput(formInputResource);
            results.trackResult(() -> mapFormInputResponse(results, formInputResource, applicationId, applicants), applicantFormInputResource::setApplicantResponses);
        }));
        return serviceSuccess(applicantFormInputResources);
    }

    private ServiceResult<List<ApplicantFormInputResponseResource>> mapFormInputResponse(ServiceResults results, FormInputResource formInputResource, Long applicationId, List<ApplicantResource> applicants) {
        List<ApplicantFormInputResponseResource> responses = new ArrayList<>();
        results.trackResult(() -> formInputService.findResponsesByFormInputIdAndApplicationId(formInputResource.getId(), applicationId),
                formInputResponseResources -> {
                    formInputResponseResources.forEach(formInputResponseResource -> {
                        ApplicantFormInputResponseResource applicantResponse = new ApplicantFormInputResponseResource();
                        applicantResponse.setResponse(formInputResponseResource);
                        applicantResponse.setApplicant(applicants.stream()
                                .filter(applicant -> formInputResponseResource.getUpdatedBy().equals(applicant.getProcessRole().getId()))
                                .findAny().orElse(null));
                        responses.add(applicantResponse);
                    });
                });
        return serviceSuccess(responses);
    }

    private ServiceResult<ApplicantResource> toApplicant(ServiceResults results, ProcessRoleResource role) {
        ApplicantResource applicantResource = new ApplicantResource();
        results.trackResult(() -> organisationService.findById(role.getOrganisationId()), applicantResource::setOrganisation);
        applicantResource.setProcessRole(role);
        return serviceSuccess(applicantResource);
    }


    private class ServiceResults {
        private List<ServiceResult<Void>> results = new ArrayList<>();

        private <ResultType> void trackResult(Supplier<ServiceResult<ResultType>> resultSupplier, Consumer<ResultType> consumer) {
            if (isSuccessful()) {
                results.add(resultSupplier.get().andOnSuccessReturnVoid(consumer));
            }
        }

        public boolean isSuccessful() {
            return results.stream().noneMatch(ServiceResult::isFailure);
        }

        private ServiceResult<Void> toSingle() {
            return aggregate(results).andOnSuccessReturnVoid();
        }
    }
}
