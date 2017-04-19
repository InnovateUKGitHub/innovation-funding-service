package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.transactional.ProjectStatusService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.file.controller.FileControllerUtils.*;
import static java.util.Optional.ofNullable;

/**
 * ProjectController exposes Project data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectStatusService projectStatusService;

    @Autowired
    private ProjectGrantOfferService projectGrantOfferService;

    @Autowired
    @Qualifier("projectSetupOtherDocumentsFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @Autowired
    private UserAuthenticationService userAuthenticationService;


    @GetMapping("/{id}")
    public RestResult<ProjectResource> getProjectById(@PathVariable("id") final Long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @GetMapping("/application/{application}")
    public RestResult<ProjectResource> getByApplicationId(@PathVariable("application") final Long application) {
        return projectService.getByApplicationId(application).toGetResponse();
    }

    @GetMapping("/")
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }

    @PostMapping(value="/{id}/project-manager/{projectManagerId}")
    public RestResult<Void> setProjectManager(@PathVariable("id") final Long id, @PathVariable("projectManagerId") final Long projectManagerId) {
        return projectService.setProjectManager(id, projectManagerId).toPostResponse();
    }

    @PostMapping("/{projectId}/startdate")
    public RestResult<Void> updateProjectStartDate(@PathVariable("projectId") final Long projectId,
                                                   @RequestParam("projectStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectStartDate) {
        return projectService.updateProjectStartDate(projectId, projectStartDate).toPostResponse();
    }

    @PostMapping("/{projectId}/address")
    public RestResult<Void> updateProjectAddress(@PathVariable("projectId") final Long projectId,
                                                 @RequestParam("leadOrganisationId") final Long leadOrganisationId,
                                                 @RequestParam("addressType") final String addressType,
                                                 @RequestBody AddressResource addressResource) {
        return projectService.updateProjectAddress(leadOrganisationId, projectId, OrganisationAddressType.valueOf(addressType), addressResource).toPostResponse();
    }

    @GetMapping(value = "/user/{userId}")
    public RestResult<List<ProjectResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return projectService.findByUserId(userId).toGetResponse();
    }

    @PostMapping("/{projectId}/organisation/{organisation}/finance-contact")
    public RestResult<Void> updateFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @PathVariable("organisation") final Long organisationId,
                                                 @RequestParam("financeContact") Long financeContactUserId) {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        return projectService.updateFinanceContact(composite, financeContactUserId).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-finance-contact")
    public RestResult<Void> inviteFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectService.inviteFinanceContact(projectId, inviteResource).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-project-manager")
    public RestResult<Void> inviteProjectManager(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectService.inviteProjectManager(projectId, inviteResource).toPostResponse();
    }

    @GetMapping("/{projectId}/project-users")
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable("projectId") final Long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @PostMapping("/{projectId}/setApplicationDetailsSubmitted")
    public RestResult<Void> setApplicationDetailsSubmitted(@PathVariable("projectId") final Long projectId){
        return projectService.submitProjectDetails(projectId, ZonedDateTime.now()).toPostResponse();
    }

    @GetMapping("/{projectId}/isSubmitAllowed")
    public RestResult<Boolean> isSubmitAllowed(@PathVariable("projectId") final Long projectId){
        return projectService.isSubmitAllowed(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/getOrganisationByUser/{userId}")
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @PostMapping(value = "/{projectId}/collaboration-agreement", produces = "application/json")
    public RestResult<FileEntryResource> addCollaborationAgreementDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.createCollaborationAgreementFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier)
        );
    }

    @GetMapping("/{projectId}/collaboration-agreement")
    public @ResponseBody
    ResponseEntity<Object> getCollaborationAgreementFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        return handleFileDownload(() -> projectService.getCollaborationAgreementFileContents(projectId));
    }

    @GetMapping(value = "/{projectId}/collaboration-agreement/details", produces = "application/json")
    public RestResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.getCollaborationAgreementFileEntryDetails(projectId).toGetResponse();
    }


    @PutMapping(value = "/{projectId}/collaboration-agreement", produces = "application/json")
    public RestResult<Void> updateCollaborationAgreementDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.updateCollaborationAgreementFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/{projectId}/collaboration-agreement", produces = "application/json")
    public RestResult<Void> deleteCollaborationAgreementDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.deleteCollaborationAgreementFile(projectId).toDeleteResponse();
    }

    @PostMapping(value = "/{projectId}/exploitation-plan", produces = "application/json")
    public RestResult<FileEntryResource> addExploitationPlanDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.createExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @GetMapping("/{projectId}/exploitation-plan")
    public @ResponseBody
    ResponseEntity<Object> getExploitationPlanFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        return handleFileDownload(() -> projectService.getExploitationPlanFileContents(projectId));
    }

    @GetMapping(value = "/{projectId}/exploitation-plan/details", produces = "application/json")
    public RestResult<FileEntryResource> getExploitationPlanFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.getExploitationPlanFileEntryDetails(projectId).toGetResponse();
    }

    @PutMapping(value = "/{projectId}/exploitation-plan", produces = "application/json")
    public RestResult<Void> updateExploitationPlanDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.updateExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @DeleteMapping(value = "/{projectId}/exploitation-plan", produces = "application/json")
    public RestResult<Void> deleteExploitationPlanDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.deleteExploitationPlanFile(projectId).toDeleteResponse();
    }

    @PostMapping("/{projectId}/partner/documents/approved/{approved}")
    public RestResult<Void> acceptOrRejectOtherDocuments(@PathVariable("projectId") long projectId, @PathVariable("approved") Boolean approved) {
        //TODO INFUND-7493
        return projectService.acceptOrRejectOtherDocuments(projectId, approved).toPostResponse();

    }

    @GetMapping("/{projectId}/partner/documents/ready")
    public RestResult<Boolean>isOtherDocumentsSubmitAllowed(@PathVariable("projectId") final Long projectId,
                                                            HttpServletRequest request) {

        UserResource authenticatedUser = userAuthenticationService.getAuthenticatedUser(request);
        return projectService.isOtherDocumentsSubmitAllowed(projectId, authenticatedUser.getId()).toGetResponse();
    }

    @PostMapping("/{projectId}/partner/documents/submit")
    public RestResult<Void>setPartnerDocumentsSubmitted(@PathVariable("projectId") final Long projectId) {
        return projectService.saveDocumentsSubmitDateTime(projectId, ZonedDateTime.now()).toPostResponse();
    }

    @PostMapping("/{projectId}/partners")
    public RestResult<Void> addPartner(@PathVariable(value = "projectId")Long projectId,
                                       @RequestParam(value = "userId", required = true) Long userId,
                                       @RequestParam(value = "organisationId", required = true) Long organisationId) {
        return projectService.addPartner(projectId, userId, organisationId).toPostResponse();
    }

    @GetMapping("/{projectId}/team-status")
    public RestResult<ProjectTeamStatusResource> getTeamStatus(@PathVariable(value = "projectId") Long projectId,
                                       @RequestParam(value = "filterByUserId", required = false) Long filterByUserId) {
        return projectService.getProjectTeamStatus(projectId, ofNullable(filterByUserId)).toGetResponse();
    }

    @GetMapping("/{projectId}/project-manager")
    public RestResult<ProjectUserResource> getProjectManager(@PathVariable(value = "projectId") Long projectId) {
        return projectService.getProjectManager(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/status")
    public RestResult<ProjectStatusResource> getStatus(@PathVariable(value = "projectId") Long projectId) {
        return projectStatusService.getProjectStatusByProjectId(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/is-send-grant-offer-letter-allowed")
    public RestResult<Boolean> isSendGrantOfferLetterAllowed(@PathVariable("projectId") final Long projectId) {
        return projectService.isSendGrantOfferLetterAllowed(projectId).toGetResponse();
    }
    @PostMapping("/{projectId}/grant-offer/send")
    public RestResult<Void> sendGrantOfferLetter(@PathVariable("projectId") final Long projectId) {
        return projectService.sendGrantOfferLetter(projectId).toPostResponse();
    }

    @GetMapping("/{projectId}/is-grant-offer-letter-already-sent")
    public RestResult<Boolean> isGrantOfferLetterAlreadySent(@PathVariable("projectId") final Long projectId) {
        return projectService.isGrantOfferLetterAlreadySent(projectId).toGetResponse();
    }

    @PostMapping("/{projectId}/signed-grant-offer-letter/approval/{approvalType}")
    public RestResult<Void> approveOrRejectSignedGrantOfferLetter(@PathVariable("projectId") final Long projectId,
                                                                  @PathVariable("approvalType") final ApprovalType approvalType) {
        return projectService.approveOrRejectSignedGrantOfferLetter(projectId, approvalType).toPostResponse();
    }

    @GetMapping("/{projectId}/signed-grant-offer-letter/approval")
    public RestResult<Boolean> isSignedGrantOfferLetterApproved(@PathVariable("projectId") final Long projectId) {
        return projectService.isSignedGrantOfferLetterApproved(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/grant-offer-letter/state")
    public RestResult<GOLState> getGrantOfferLetterWorkflowState(@PathVariable("projectId") final Long projectId) {
        return projectService.getGrantOfferLetterWorkflowState(projectId).toGetResponse();
    }
}
