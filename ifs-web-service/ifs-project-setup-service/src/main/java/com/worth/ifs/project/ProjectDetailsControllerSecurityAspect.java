package com.worth.ifs.project;

import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This Advice targets any public @RequestMapping methods in ProjectDetailsController that can supply a projectId as
 * its first argument.  Based on the projectId this class looks up the current ProjectTeamStatus and decides whether or
 * not the current Partner is able to access the Project Details section
 */
@Aspect
@Component
public class ProjectDetailsControllerSecurityAspect {

    @Autowired
    private ProjectDetailsControllerSecurityAdvisor advisor;

    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && " +
            "execution(public java.lang.String com.worth.ifs.project.ProjectDetailsController.*(..)) && " +
            "args(projectId, ..)")
    public void checkAccessToProjectDetailsSection(Long projectId) throws Throwable {

        if (!advisor.canAccessProjectDetailsSection(projectId)) {
            throw new ForbiddenActionException("Unable to access the Project Details section at this time");
        }
    }
}
