package com.worth.ifs.project.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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
                                                  @RequestBody final MonitoringOfficerResource monitoringOfficerResource) {

        return projectService.saveMonitoringOfficer(projectId, monitoringOfficerResource).toPutResponse();
    }
    @RequestMapping(value = "/{projectId}/getOrganisationByUser/{userId}", method = GET)
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @RequestMapping(value = "/{projectId}/bank-details", method = POST)
    public RestResult<Void> updateBanksDetail(@PathVariable("projectId") final Long projectId,
                                              @RequestBody BankDetailsResource bankDetailsResource){
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
}
