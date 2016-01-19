package com.worth.ifs.user.transactional;

import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.user.transactional.RegistrationServiceImpl.ServiceFailures.DUPLICATE_EMAIL_ADDRESS;

/**
 *
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {

    public enum ServiceFailures {
        DUPLICATE_EMAIL_ADDRESS
    }

    @Autowired
    private IdentityProviderService idpService;
    
    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<User> createUserLeadApplicantForOrganisation(Long organisationId, UserResource userResource) {

        User newUser = assembleUserFromResource(userResource);
        addOrganisationToUser(newUser, organisationId);
        addRoleToUser(newUser, UserRoleType.APPLICANT.getName());

        if(userRepository.findByEmail(userResource.getEmail()).isEmpty()) {
            return createUserWithUid(newUser, userResource.getPassword());
        }
        else {

            // TODO DW - INFUND-1267 - this type of check and error should be returned from the REST API
            return failureResponse(DUPLICATE_EMAIL_ADDRESS);
        }
    }


    private ServiceResult<User> createUserWithUid(User user, String password) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getTitle(), user.getFirstName(), user.getLastName(), user.getEmail(), password);

        return uidFromIdpResult.map(uidFromIdp -> {
            user.setUid(uidFromIdp);
            User createdUser = userRepository.save(user);
            return successResponse(createdUser);
        });
    }

    private void addRoleToUser(User user, String roleName) {
        List<Role> userRoles = roleRepository.findByName(roleName);
        user.setRoles(userRoles);
    }

    private void addOrganisationToUser(User user, Long organisationId) {
        Organisation userOrganisation = organisationRepository.findOne(organisationId);
        List<Organisation> userOrganisationList = new ArrayList<>();
        userOrganisationList.add(userOrganisation);
        user.setOrganisations(userOrganisationList);
    }

    private User assembleUserFromResource(UserResource userResource) {
        User newUser = new User();
        newUser.setFirstName(userResource.getFirstName());
        newUser.setLastName(userResource.getLastName());
        newUser.setPassword(userResource.getPassword());
        newUser.setEmail(userResource.getEmail());
        newUser.setTitle(userResource.getTitle());
        newUser.setPhoneNumber(userResource.getPhoneNumber());

        String fullName = concatenateFullName(userResource.getFirstName(), userResource.getLastName());
        newUser.setName(fullName);

        return newUser;
    }

    private String concatenateFullName(String firstName, String lastName) {
        return firstName+" "+lastName;
    }
}
