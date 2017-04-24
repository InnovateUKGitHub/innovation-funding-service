package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.LdapProperties;
import org.innovateuk.ifs.shibboleth.api.PasswordPolicyProperties;
import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.exceptions.PasswordPolicyException;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.models.validators.EmailValidator;
import org.innovateuk.ifs.shibboleth.api.models.validators.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.*;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IdentityServices implements CreateIdentityService, FindIdentityService, UpdateIdentityService,
    DeleteIdentityService, ActivateUserService, UserAccountLockoutService {

    @Autowired
    private LdapProperties ldapProperties;

    private final LdapTemplate ldapTemplate;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final String lockedUntilAttrib = "pwdAccountLockedTime";
    private final String pwdLockoutDurationAttrib = "pwdLockoutDuration";

    // note "X" here denotes an ISO timezone identifier (we're expecting "Z" to indicate UTC)
    // 2 date formats because the lock field can (depending on the server config) include milliseconds
    private final String lockDateFormatNoMillis = "yyyyMMddHHmmssX";
    private final String lockDateFormatWithMillis = "yyyyMMddHHmmss.SSSX";

    private static final Logger LOG = LoggerFactory.getLogger(IdentityServices.class);

    @Autowired
    public IdentityServices(final LdapTemplate ldapTemplate, final PasswordPolicyProperties passwordPolicy) {

        this.ldapTemplate = ldapTemplate;

        this.passwordValidator = new PasswordValidator(passwordPolicy);
        this.emailValidator = new EmailValidator(this);
    }

    /**
     * checks that the password policy can be retrieved from the LDAP server. If the 'requireValidPPolicy'
     * flag is true, the application will only start if it can successfully retrieve and parse the password lockout time
     */
    @PostConstruct
    public void testPasswordPolicy() {
        if (ldapProperties.getRequireValidPPolicy()) {
            try {
                getPwdLockoutDuration();
            } catch (PasswordPolicyException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Identity createIdentity(final String email, final String password)
            throws DuplicateEmailException, InvalidPasswordException {

        emailValidator.validate(email);
        passwordValidator.validate(password);

        final Identity identity = new Identity(email, password, false);

        ldapTemplate.create(identity);

        return getIdentity(identity.getUuid());
    }


    @Override
    public Identity getIdentity(final UUID uuid) {

        final ContainerCriteria query = queryIdentity().where(Identity.Constants.UUID).is(uuid.toString());

        return ldapTemplate.findOne(query, Identity.class);
    }


    @Override
    public Optional<Identity> findByEmail(final String email) {

        final ContainerCriteria query = queryIdentity().where(Identity.Constants.EMAIL).like(email);

        final List<Identity> identities = ldapTemplate.find(query, Identity.class);

        if (identities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(identities.get(0));
        }
    }


    @Override
    public void deleteIdentity(final UUID uuid) {

        final Identity identity = getIdentity(uuid);

        ldapTemplate.delete(identity);
    }


    @Override
    public void changePassword(final UUID uuid, final String password) throws InvalidPasswordException {

        final Identity identity = getIdentity(uuid);
        identity.setPassword(password);

        passwordValidator.validate(password);

        updateIdentity(identity);
    }


    @Override
    public void changeEmail(final UUID uuid, final String email) throws DuplicateEmailException {

        final Identity identity = getIdentity(uuid);
        identity.setEmail(email);

        emailValidator.validate(email);

        updateIdentity(identity);
    }

    @Override
    public void activateUser(final UUID uuid) {
        final Identity identity = getIdentity(uuid);
        identity.setUserActive(true);
        updateIdentity(identity);
    }

    @Override
    public void deactivateUser(final UUID uuid) {
        final Identity identity = getIdentity(uuid);
        identity.setUserActive(false);
        updateIdentity(identity);
    }

    private void updateIdentity(final Identity identity) {

        identity.prepareForSaving();

        ldapTemplate.update(identity);
    }


    private LdapQueryBuilder queryIdentity() {
        return LdapQueryBuilder.query().attributes(Identity.Constants.ATTRIBUTES);
    }

    /**
     * Unlock an account which has been locked with the pwdAccountLockedTime operational attribute.
     * This removes the attribute if it's been set.
     * @param uuid the user LDAP uid
     *
     */
    public void unlockAccount(final UUID uuid) {
        final Attributes lockAttributes = getPwdAccountLockedTimeAttributes(uuid);
        if (lockAttributes != null && lockAttributes.size() > 0) {
            ContextExecutor executor = new ContextExecutor() {

                @Override
                public Object executeWithContext(DirContext dc) throws NamingException {
                    if (lockAttributes.size() > 0) {
                        // there's one or more lockedUntil attrib in this user - remove them all
                        dc.modifyAttributes(new LdapName("uid=" + uuid.toString()), DirContext.REMOVE_ATTRIBUTE, lockAttributes);
                    }
                    return null;
                }
            };
            ldapTemplate.executeReadWrite(executor);
        }
    }

    /**
     * Check if an account's password is locked out using the pwdAccountLockedTime and the
     * password policy's pwdLockoutDuration attribute.
     *
     * @param uuid the ldap user ID
     * @return whether the account is currently locked
     * @throws PasswordPolicyException if the password lockout time can't be retrieved from the policy
     *
     */
    public boolean getAccountLockStatus(final UUID uuid) throws PasswordPolicyException {
        long lockDuration  = getPwdLockoutDuration();
        Attributes lockAttributes = getPwdAccountLockedTimeAttributes(uuid);
        if (lockAttributes != null) {
            Attribute lockAttribute = lockAttributes.get(lockedUntilAttrib);
            if (lockAttribute != null) {
                String lockValue = "";
                try {
                    // we need to parse the value to see if this lock has expired.
                    // Here we assume this Attribute has only one value
                    lockValue = lockAttribute.get().toString();
                    Date lockDate;

                    // depending on the server configuration, the lock date may or may not include
                    // the milliseconds field
                    if (lockValue.length() == lockDateFormatNoMillis.length()) {
                        lockDate = new SimpleDateFormat(lockDateFormatNoMillis).parse(lockValue);
                    } else {
                        lockDate = new SimpleDateFormat(lockDateFormatWithMillis).parse(lockValue);
                    }
                    return lockDuration == 0 || lockDate.getTime() + (lockDuration * 1000) > System.currentTimeMillis();
                } catch (ParseException e) {
                    LOG.error("Unable to parse " + lockedUntilAttrib + " attrib value " + lockValue);
                } catch (NamingException e) {
                    LOG.error("Unable to read " + lockedUntilAttrib + " attrib value");
                }
            }
        }
        return false;
    }

    /**
     * the the Attributes for this user's pwdAccountLockedTime, or null if this entry has no such property.
     * @param uuid
     * @return
     */
    private Attributes getPwdAccountLockedTimeAttributes(final UUID uuid) {
        try {
            LdapName name = new LdapName("uid=" + uuid.toString());
            Attributes attributes = (Attributes) ldapTemplate.lookup(name, new String[]{lockedUntilAttrib},
                    new AbstractContextMapper() {
                        @Override
                        protected Object doMapFromContext(DirContextOperations ctx) {
                            return ctx.getAttributes();
                        }
                    });
            return attributes;
        } catch (InvalidNameException e) {
            LOG.error("Invalid LDAP name getting " + lockedUntilAttrib + " for user " + uuid.toString(), e);
            return null;
        }
    }

    /**
     * Get the password policy lockout duration, or 0 if this cannot be found.
     * @return
     */
    private long getPwdLockoutDuration() throws PasswordPolicyException {
        try {
            LdapName name = new LdapName(ldapProperties.getPpolicyAttrib());
            String durationStr = (String) ldapTemplate.lookup(name, new String[]{pwdLockoutDurationAttrib},
                    new AbstractContextMapper() {
                        @Override
                        protected Object doMapFromContext(DirContextOperations ctx) {
                            return ctx.getStringAttribute(pwdLockoutDurationAttrib);
                        }
                    });
            return Long.valueOf(durationStr);
        } catch (Exception e) {
            LOG.error("Unable to get password policy " + ldapProperties.getPpolicyAttrib(), e);
            throw new PasswordPolicyException();
        }
    }
}
