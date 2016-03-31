package com.worth.ifs.security;


import java.util.List;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;

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

    private static List<ProcessRole> processRoles = newProcessRole()
            .withApplication(application)
            .build(1);

    private static User basicLeadApplicant = new User(1L, "steve", "smith", "steve.smith@empire.com", "", processRoles, "123abc");


    public static final User swapOutForUser(User user) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        } else {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        }
        return authentication != null && authentication.getDetails() instanceof User ? (User) authentication.getDetails() : null;
    }

    public static final User useBasicLeadApplicant(){
        return swapOutForUser(basicLeadApplicant);
    }



}
