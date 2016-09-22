package com.worth.ifs.commons.security;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;

public class SecuritySetter {

    private static ApplicationStatus applicationStatus =  new ApplicationStatus(
            ApplicationStatusConstants.CREATED.getId(),
            ApplicationStatusConstants.CREATED.getName()
    );

    private static Application application = newApplication()
            .withId(1L)
            .withName("Application Name")
            .withApplicationStatus(applicationStatus)
            .withCompetition(newCompetition().build())
            .build();

    private static List<Organisation> organisations = newOrganisation().withId(1L, 3L, 6L).build(3);

    public static UserResource basicSecurityUser = newUserResource().withId(1L).withFirstName("steve").withLastName("smith").withEmail("steve.smith@empire.com").build();

    public static final UserResource swapOutForUser(UserResource user) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        else {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }
        return authentication != null && authentication.getDetails() instanceof UserResource ? (UserResource)authentication.getDetails() : null;
    }

    public static final UserResource addBasicSecurityUser(){
        return swapOutForUser(basicSecurityUser);
    }

}
