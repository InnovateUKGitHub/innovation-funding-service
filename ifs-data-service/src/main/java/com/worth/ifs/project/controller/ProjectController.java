package com.worth.ifs.project.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.file.controller.FileUploadControllerUtils.inputStreamSupplier;
import static org.hibernate.jpa.internal.QueryImpl.LOG;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
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
    private BankDetailsService bankDetailsService;

    @Autowired
    @Qualifier("projectSetupOtherDocumentsFileValidator")
    private FileHttpHeadersValidator fileValidator;

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

    @RequestMapping(value = "/{projectId}/project-users", method = GET)
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable("projectId") final Long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/setApplicationDetailsSubmitted", method = POST)
    public RestResult<Void> setApplicationDetailsSubmitted(@PathVariable("projectId") final Long projectId){
        return projectService.saveProjectSubmitDateTime(projectId, LocalDateTime.now()).toPostResponse();
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
                .andOnSuccess(() -> projectService.notifyMonitoringOfficer(monitoringOfficerResource))
                .toPutResponse();
    }
    @RequestMapping(value = "/{projectId}/getOrganisationByUser/{userId}", method = GET)
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/bank-details", method = POST)
    public RestResult<Void> updateBanksDetail(@PathVariable("projectId") final Long projectId,
                                              @RequestBody @Valid final BankDetailsResource bankDetailsResource){
        return bankDetailsService.updateBankDetails(bankDetailsResource).toPostResponse();
    }

    @RequestMapping(value = "/{projectId}/bank-details", method = GET, params = "bankDetailsId")
    public RestResult<BankDetailsResource> getBankDetails(@PathVariable("projectId") final Long projectId,
                                                          @RequestParam("bankDetailsId") final Long bankDetailsId){
        return bankDetailsService.getById(bankDetailsId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/bank-details", method = GET, params = "organisationId")
    public RestResult<BankDetailsResource> getBankDetailsByOrganisationId(@PathVariable("projectId") final Long projectId,
                                                                          @RequestParam("organisationId") final Long organisationId){
        return bankDetailsService.getByProjectAndOrganisation(projectId, organisationId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addCollaborationAgreementDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> fileAddedResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                        projectService.createCollaborationAgreementFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return fileAddedResult.toPostCreateResponse();
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getCollaborationAgreementFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FileEntryResource, Supplier<InputStream>>> getFileResult = projectService.getCollaborationAgreementFileEntryContents(projectId);

            return getFileResult.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    fileResult -> {
                        FileEntryResource fileEntry = fileResult.getKey();
                        Supplier<InputStream> inputStreamSupplier = fileResult.getValue();
                        InputStream inputStream = inputStreamSupplier.get();
                        ByteArrayResource inputStreamResource = new ByteArrayResource(StreamUtils.copyToByteArray(inputStream));
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
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

        ServiceResult<FileEntryResource> updateResult = fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                projectService.updateCollaborationAgreementFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/{projectId}/collaboration-agreement", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteCollaborationAgreementDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        return projectService.deleteCollaborationAgreementFileEntry(projectId).toDeleteResponse();
    }


    @RequestMapping(value = "/{projectId}/exploitation-plan", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addExploitationPlanDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @PathVariable(value = "projectId") long projectId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> fileAddedResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                        projectService.createExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return fileAddedResult.toPostCreateResponse();
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getExploitationPlanFileContents(
            @PathVariable("projectId") long projectId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FileEntryResource, Supplier<InputStream>>> getFileResult = projectService.getExploitationPlanFileEntryContents(projectId);

            return getFileResult.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    fileResult -> {
                        FileEntryResource fileEntry = fileResult.getKey();
                        Supplier<InputStream> inputStreamSupplier = fileResult.getValue();
                        InputStream inputStream = inputStreamSupplier.get();
                        ByteArrayResource inputStreamResource = new ByteArrayResource(StreamUtils.copyToByteArray(inputStream));
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
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

        ServiceResult<FileEntryResource> updateResult = fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                projectService.updateExploitationPlanFileEntry(projectId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/{projectId}/exploitation-plan", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteExploitationPlanDocument(
            @PathVariable("projectId") long projectId) throws IOException {

        ServiceResult<Void> deleteResult = projectService.deleteExploitationPlanFileEntry(projectId);
        return deleteResult.toDeleteResponse();
    }
}
