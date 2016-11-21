package com.worth.ifs.project.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.project.transactional.ProjectStatusService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.file.controller.FileControllerUtils.*;
import static java.util.Optional.ofNullable;
import static org.springframework.web.bind.annotation.RequestMethod.*;

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
    @Qualifier("projectSetupOtherDocumentsFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @Autowired
    private UserAuthenticationService userAuthenticationService;


    @RequestMapping("/{id}")
    public RestResult<ProjectResource> getProjectById(@PathVariable("id") final Long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @RequestMapping("/application/{application}")
    public RestResult<ProjectResource> getByApplicationId(@PathVariable("application") final Long application) {
        return projectService.getByApplicationId(application).toGetResponse();
    }

    @RequestMapping("/")
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/{id}/project-manager/{projectManagerId}")
    public RestResult<Void> setProjectManager(@PathVariable("id") final Long id, @PathVariable("projectManagerId") final Long projectManagerId) {
        return projectService.setProjectManager(id, projectManagerId).toPostResponse();
    }
    
    @RequestMapping(value = "/{projectId}/startdate", method = POST)
    public RestResult<Void> updateProjectStartDate(@PathVariable("projectId") final Long projectId,
                                                   @RequestParam("projectStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectStartDate) {
        return projectService.updateProjectStartDate(projectId, projectStartDate).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/address", method = POST)
    public RestResult<Void> updateProjectAddress(@PathVariable("projectId") final Long projectId,
                                                 @RequestParam("leadOrganisationId") final Long leadOrganisationId,
                                                 @RequestParam("addressType") final String addressType,
                                                 @RequestBody AddressResource addressResource) {
        return projectService.updateProjectAddress(leadOrganisationId, projectId, OrganisationAddressType.valueOf(addressType), addressResource).toPostResponse();
    }

    @RequestMapping(value = "/user/{userId}")
    public RestResult<List<ProjectResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return projectService.findByUserId(userId).toGetResponse();
    }
    
    @RequestMapping(value = "/{projectId}/organisation/{organisation}/finance-contact", method = POST)
    public RestResult<Void> updateFinanceContact(@PathVariable("projectId") final Long projectId,
    		@PathVariable("organisation") final Long organisationId,
                                                   @RequestParam("financeContact") Long financeContactUserId) {
        return projectService.updateFinanceContact(projectId, organisationId, financeContactUserId).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/invite-finance-contact", method = POST)
    public RestResult<Void> inviteFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
       return projectService.inviteFinanceContact(projectId, inviteResource).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/invite-project-manager", method = POST)
    public RestResult<Void> inviteProjectManager(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectService.inviteProjectManager(projectId, inviteResource).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/project-users", method = GET)
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable("projectId") final Long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/setApplicationDetailsSubmitted", method = POST)
    public RestResult<Void> setApplicationDetailsSubmitted(@PathVariable("projectId") final Long projectId){
        return projectService.submitProjectDetails(projectId, LocalDateTime.now()).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/isSubmitAllowed", method = GET)
    public RestResult<Boolean> isSubmitAllowed(@PathVariable("projectId") final Long projectId){
        return projectService.isSubmitAllowed(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/monitoring-officer", method = GET)
    public RestResult<MonitoringOfficerResource> getMonitoringOfficer(@PathVariable("projectId") final Long projectId) {
        return projectService.getMonitoringOfficer(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/monitoring-officer", method = PUT)
    public RestResult<Void> saveMonitoringOfficer(@PathVariable("projectId") final Long projectId,
                                                  @RequestBody @Valid final MonitoringOfficerResource monitoringOfficerResource) {
        return projectService.saveMonitoringOfficer(projectId, monitoringOfficerResource)
                .andOnSuccess(() -> projectService.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource))
                .toPutResponse();
    }

    @RequestMapping(value = "/{projectId}/getOrganisationByUser/{userId}", method = GET)
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = POST, produces = "application/json")
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

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getCollaborationAgreementFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        return handleFileDownload(() -> projectService.getCollaborationAgreementFileContents(projectId));
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement/details", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.getCollaborationAgreementFileEntryDetails(projectId).toGetResponse();
    }


    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = PUT, produces = "application/json")
    public RestResult<Void> updateCollaborationAgreementDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.updateCollaborationAgreementFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteCollaborationAgreementDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.deleteCollaborationAgreementFile(projectId).toDeleteResponse();
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addExploitationPlanDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.createExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getExploitationPlanFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        return handleFileDownload(() -> projectService.getExploitationPlanFileContents(projectId));
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan/details", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getExploitationPlanFileEntryDetails(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.getExploitationPlanFileEntryDetails(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = PUT, produces = "application/json")
    public RestResult<Void> updateExploitationPlanDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        return handleFileUpdate(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                projectService.updateExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier));
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteExploitationPlanDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.deleteExploitationPlanFile(projectId).toDeleteResponse();
    }

    @RequestMapping(value = "/{projectId}/partner/documents/approved/{approved}", method = POST)
    public RestResult<Void> acceptOrRejectOtherDocuments(@PathVariable("projectId") long projectId, @PathVariable("approved") Boolean approved) {

        return projectService.acceptOrRejectOtherDocuments(projectId, approved).toPostResponse();

    }

    @RequestMapping(value = "/{projectId}/partner/documents/ready", method = GET)
    public RestResult<Boolean>isOtherDocumentsSubmitAllowed(@PathVariable("projectId") final Long projectId,
                                                            HttpServletRequest request) {

        UserResource authenticatedUser = userAuthenticationService.getAuthenticatedUser(request);
        return projectService.isOtherDocumentsSubmitAllowed(projectId, authenticatedUser.getId()).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/partner/documents/submit", method = POST)
    public RestResult<Void>setPartnerDocumentsSubmitted(@PathVariable("projectId") final Long projectId) {
        return projectService.saveDocumentsSubmitDateTime(projectId, LocalDateTime.now()).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/partners", method = POST)
    public RestResult<Void> addPartner(@PathVariable(value = "projectId")Long projectId,
                                       @RequestParam(value = "userId", required = true) Long userId,
                                       @RequestParam(value = "organisationId", required = true) Long organisationId) {
        return projectService.addPartner(projectId, userId, organisationId).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/team-status", method = GET)
    public RestResult<ProjectTeamStatusResource> getTeamStatus(@PathVariable(value = "projectId") Long projectId,
                                                               @RequestParam(value = "filterByUserId", required = false) Long filterByUserId) {
        return projectService.getProjectTeamStatus(projectId, ofNullable(filterByUserId)).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/status", method = GET)
    public RestResult<ProjectStatusResource> getStatus(@PathVariable(value = "projectId") Long projectId) {
        return projectStatusService.getProjectStatusByProjectId(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/isSendGrantOfferLetterAllowed", method = GET)
    public RestResult<Boolean> isSendGrantOfferLetterAllowed(@PathVariable("projectId") final Long projectId) {
        return projectService.isSendGrantOfferLetterAllowed(projectId).toGetResponse();
    }
    @RequestMapping(value = "/{projectId}/grant-offer/send/{userId}", method = POST)
    public RestResult<Void> sendGrantOfferLetter(@PathVariable("projectId") Long projectId,
                                                 @PathVariable("userId") Long userId) {
        return projectService.sendGrantOfferLetter(projectId, userId).toPostResponse();
    }

    @RequestMapping(value= "/{projectId}/isGrantOfferLetterAlreadySent", method = GET)
    public RestResult<Boolean> isGrantOfferLetterAlreadySent(@PathVariable("projectId") final Long projectId) {
        return projectService.isGrantOfferLetterAlreadySent(projectId).toGetResponse();
    }
}
