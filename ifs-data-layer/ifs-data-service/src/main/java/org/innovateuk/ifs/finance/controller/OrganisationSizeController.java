package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * This RestController exposes CRUD operations to both the
 * {@link OrganisationSizeResource} and other REST-API users
 * to manage {@link org.innovateuk.ifs.finance.domain.OrganisationSize} related data.
 */
@RestController
@RequestMapping("/organisation-size")
public class OrganisationSizeController {

    @GetMapping
    @ZeroDowntime(reference = "IFS-3818", description = "To support instances of older REST clients before " +
            "the OrganisationSize enum was introduced")
    public RestResult<List<OrganisationSizeResource>> getOrganisationSizes() {
        return restSuccess(Stream.of(OrganisationSize.values()).map(this::map).collect(toList()));
    }

    private OrganisationSizeResource map(OrganisationSize organisationSize) {
        return new OrganisationSizeResource(organisationSize.getId(), organisationSize.getDescription());
    }
}
