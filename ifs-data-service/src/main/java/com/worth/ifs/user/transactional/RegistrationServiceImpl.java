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

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.ORGANISATION_NOT_FOUND;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.ROLE_NOT_FOUND;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.transactional.RegistrationServiceImpl.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.EntityLookupCallbacks.getOrFail;

/**
 * A service around Registration and general user-creation operations
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER
    }

    @Autowired
    private IdentityProviderService idpService;
    
    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<User> createUserLeadApplicantForOrganisation(Long organisationId, UserResource userResource) {

        return ServiceResult.handlingErrors(UNABLE_TO_CREATE_USER, () -> {

            User newUser = assembleUserFromResource(userResource);

            return addOrganisationToUser(newUser, organisationId).map(user ->
                   addRoleToUser(user, UserRoleType.APPLICANT.getName())).map(user ->
                   createUserWithUid(newUser, userResource.getPassword())
            );
        });
    }

    private ServiceResult<User> createUserWithUid(User user, String password) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.map(uidFromIdp -> {
            user.setUid(uidFromIdp);
            User createdUser = userRepository.save(user);
            return successResponse(createdUser);
        });
    }

    private ServiceResult<User> addRoleToUser(User user, String roleName) {

        return getOrFail(() -> roleRepository.findByName(roleName), ROLE_NOT_FOUND).map(roles -> {

            Role applicantRole = getOnlyElement(roles);

            List<Role> newRoles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();

            if (!newRoles.contains(applicantRole)) {
                newRoles.add(applicantRole);
            }

            user.setRoles(newRoles);
            return success(user);
        });

    }

    private ServiceResult<User> addOrganisationToUser(User user, Long organisationId) {

        return getOrFail(() -> organisationRepository.findOne(organisationId), ORGANISATION_NOT_FOUND).map(userOrganisation -> {

            List<Organisation> userOrganisationList = new ArrayList<>();
            userOrganisationList.add(userOrganisation);
            user.setOrganisations(userOrganisationList);
            return success(user);
        });
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
