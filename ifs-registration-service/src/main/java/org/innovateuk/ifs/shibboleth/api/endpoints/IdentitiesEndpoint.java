package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.exceptions.PasswordPolicyException;
import org.innovateuk.ifs.shibboleth.api.models.ChangeEmail;
import org.innovateuk.ifs.shibboleth.api.models.ChangePassword;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.models.NewIdentity;
import org.innovateuk.ifs.shibboleth.api.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/identities")
public class IdentitiesEndpoint implements RestExceptionHandlers, LdapExceptionHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(IdentitiesEndpoint.class);

    private final CreateIdentityService createService;
    private final FindIdentityService findService;
    private final DeleteIdentityService deleteService;
    private final UpdateIdentityService updateService;
    private final ActivateUserService activateUserService;
    private final UserAccountLockoutService userAccountLockoutService;

    @Autowired
    public IdentitiesEndpoint(final CreateIdentityService createService, final FindIdentityService findService,
        final DeleteIdentityService deleteService, final UpdateIdentityService updateService,
                              final ActivateUserService activateUserService, UserAccountLockoutService userAccountLockoutService) {

        this.createService = createService;
        this.findService = findService;
        this.deleteService = deleteService;
        this.updateService = updateService;
        this.activateUserService = activateUserService;
        this.userAccountLockoutService = userAccountLockoutService;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Identity> createIdentity(@Valid @RequestBody final NewIdentity newIdentity)
        throws DuplicateEmailException, InvalidPasswordException {

        LOG.debug("create request: {}", newIdentity);

        final Identity identity = createService.createIdentity(
            newIdentity.getEmail(),
            newIdentity.getPassword()
        );

        LOG.debug("created Identity: {}", identity);

        return ResponseEntity.
            created(
                URI.create("/identities/" + identity.getUuid())
            ).
            body(
                identity
            );
    }


    @RequestMapping(path = "/{uuid}", method = RequestMethod.GET)
    public Identity getIdentity(@PathVariable final UUID uuid) {

        LOG.debug("get request: UUID [{}]", uuid);

        return findService.getIdentity(uuid);
    }


    @RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteIdentity(@PathVariable final UUID uuid) {

        LOG.debug("delete request: UUID [{}]", uuid);

        deleteService.deleteIdentity(uuid);

        return ResponseEntity.ok().build();
    }


    @RequestMapping(path = "/{uuid}/email", method = RequestMethod.PUT)
    public ResponseEntity<Void> changeEmail(@PathVariable final UUID uuid, @Valid @RequestBody final ChangeEmail change)
        throws DuplicateEmailException {

        LOG.debug("change email request: UUID [{}], [{}]", uuid, change);

        updateService.changeEmail(uuid, change.getEmail());

        return ResponseEntity.ok().build();
    }


    @RequestMapping(path = "/{uuid}/password", method = RequestMethod.PUT)
    public ResponseEntity<Void> changePassword(@PathVariable final UUID uuid,
        @Valid @RequestBody final ChangePassword change) throws InvalidPasswordException {

        LOG.debug("change password request: UUID [{}], [{}]", uuid, change);

        updateService.changePassword(uuid, change.getPassword());

        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/{uuid}/activateUser", method = RequestMethod.PUT)
    public ResponseEntity<Void> activateUser(@PathVariable final UUID uuid) {

        LOG.debug("validate email request: UUID [{}]", uuid);

        activateUserService.activateUser(uuid);

        return ResponseEntity.ok().build();
    }


    @RequestMapping(path = "/{uuid}/deactivateUser", method = RequestMethod.PUT)
    public ResponseEntity<Void> deactivateUser(@PathVariable final UUID uuid) {

        LOG.debug("validate email request: UUID [{}]", uuid);

        activateUserService.deactivateUser(uuid);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/{uuid}/lock", method = RequestMethod.DELETE)
    public ResponseEntity<Void> unlockAccount(@PathVariable final UUID uuid) {
        LOG.debug("unlock account request: UUID [{}]", uuid);
        userAccountLockoutService.unlockAccount(uuid);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/{uuid}/lock", method = RequestMethod.GET)
    public Boolean getAccountLockStatus(@PathVariable final UUID uuid) throws PasswordPolicyException {
        LOG.debug("get account lock request: UUID [{}]", uuid);
        return userAccountLockoutService.getAccountLockStatus(uuid);
    }
}
