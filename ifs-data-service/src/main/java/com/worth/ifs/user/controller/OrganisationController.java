package com.worth.ifs.user.controller;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.UserApplicationRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    UserApplicationRoleRepository userApplicationRoleRepository;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public Set<Organisation> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        List<UserApplicationRole> roles = userApplicationRoleRepository.findByApplicationId(applicationId);
        Set<Organisation> organisations = new LinkedHashSet<>();
        for (UserApplicationRole role : roles) {
            organisations.add(organisationRepository.findByUserApplicationRoles(role));
        }

        return organisations;
    }

}
