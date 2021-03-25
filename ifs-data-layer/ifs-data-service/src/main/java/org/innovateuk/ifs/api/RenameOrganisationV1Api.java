package org.innovateuk.ifs.api;

import io.swagger.annotations.ApiOperation;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface RenameOrganisationV1Api {

    @ApiOperation(value = "Find organisations by companies house number")
    @GetMapping("/find-organisations-by-companies-house-number/{companiesHouseNumber}")
    RestResult<List<OrganisationResource>> findOrganisationsByCompaniesHouseNumber(
            @PathVariable("companiesHouseNumber") final String companiesHouseNumber);

    @ApiOperation(value = "Find organisations by name")
    @GetMapping("/find-organisations-by-name/{name}")
    RestResult<List<OrganisationResource>> findOrganisationsByName(@PathVariable("name") final String name);

    @ApiOperation(value = "Update organisation name and registration")
    @PostMapping("/update-name-and-registration/{organisationId}")
    RestResult<OrganisationResource> updateNameAndRegistration(
            @PathVariable("organisationId") Long organisationId,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "registration") String registration);

}
