package org.innovateuk.ifs.api;

import io.swagger.annotations.ApiOperation;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface RenameOrganisationV1Api {

    @ApiOperation(value = "Find organisations by companies house number")
    @GetMapping("/find-organisations-by-companies-house-number/{companiesHouseNumber}")
    RestResult<List<OrganisationResource>> findOrganisationsByCompaniesHouseNumber(
            @PathVariable final String companiesHouseNumber);

    @ApiOperation(value = "Find organisations by name")
    @GetMapping("/find-organisations-by-name/{name}")
    RestResult<List<OrganisationResource>> findOrganisationsByName(@PathVariable final String name);

    @ApiOperation(value = "Update organisation name by organisationId")
    @PatchMapping("/update-organisation-name/{organisationId}")
    RestResult<OrganisationResource> updateOrganisationName(@PathVariable Long organisationId, @RequestBody String name);

}
