package com.worth.ifs.user.controller;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public Set<Organisation> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<Organisation> organisations = new LinkedHashSet<>();
        for (ProcessRole role : roles) {
            organisations.add(organisationRepository.findByProcessRoles(role));
        }

        return organisations;
    }

}
