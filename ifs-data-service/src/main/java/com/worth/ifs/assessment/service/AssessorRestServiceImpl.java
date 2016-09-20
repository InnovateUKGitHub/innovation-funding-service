package com.worth.ifs.assessment.service;


import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessorRestServiceImpl extends BaseRestService implements AssessorRestService {
    private static final String registerUserByHashUrl = "/register";

    @Override
    public RestResult<UserResource> createAssessorByInviteHash(String hash, String firstName, String lastName, String password, String email, String title, String phoneNumber, String gender, String disability, Long ethnicity) {
        UserResource user = new UserResource();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);
        user.setEthnicity(ethnicity);
        user.setGender(gender);
        user.setDisability(disability);

        return postWithRestResultAnonymous(format("%s/%s/%s", registerUserByHashUrl, hash), user, UserResource.class);
    }
}
