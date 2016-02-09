package com.worth.ifs.commons.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

/**
 * A utility for commonly used ParameterizedTypeReferences
 */
public class ParameterizedTypeReferences {

    public static ParameterizedTypeReference<List<ApplicationResource>> applicationResourceListType() {
        return new ParameterizedTypeReference<List<ApplicationResource>>() {};
    }

    public static ParameterizedTypeReference<List<ProcessRole>> processRoleListType() {
        return new ParameterizedTypeReference<List<ProcessRole>>() {};
    }

    public static ParameterizedTypeReference<List<User>> userListType() {
        return new ParameterizedTypeReference<List<User>>() {};
    }

    public static ParameterizedTypeReference<List<UserResource>> userResourceListType() {
        return new ParameterizedTypeReference<List<UserResource>>() {};
    }

    public static ParameterizedTypeReference<List<Competition>> competitionListType() {
        return new ParameterizedTypeReference<List<Competition>>() {};
    }

    public static ParameterizedTypeReference<List<Long>> longsListType() {
        return new ParameterizedTypeReference<List<Long>>() {};
    }
}
