package org.innovateuk.ifs.shibboleth.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import static org.innovateuk.ifs.shibboleth.api.models.Identity.Constants.*;

@Entry(objectClasses = { "inetOrgPerson", "person", "top"})
public final class Identity {

    @Id
    private Name dn;

    @Attribute(name = UUID)
    private UUID uuid;

    @Attribute(name = EMAIL)
    private String email;

    @JsonIgnore
    @Attribute(name = PASSWORD, type = Attribute.Type.BINARY)
    private byte[] password;

    @Attribute(name = CREATED)
    private String created;

    @Attribute(name = MODIFIED)
    private String modified;

    // Attributes enforced by LDAP schema but not required.
    @JsonIgnore
    private String sn = " ";
    @JsonIgnore
    private String cn = " ";

    /**
     * the USER_STATUS is stored in the employeeType attribute of the standard inetOrgPerson
     * schema. This is "active" or "inactive".
     */
    @Attribute(name = USER_STATUS)
    private String userStatus;

    public Identity() {
    }

    public Identity(final String email, final String password, final boolean active) {
        this(java.util.UUID.randomUUID(), email, password, active);
    }

    public Identity(final UUID uuid, final String email, final String password, final boolean active) {
        this.uuid = uuid;
        this.email = email;
        this.password = password.getBytes(Charset.forName("UTF-8"));
        setUserActive(active);
        this.dn = LdapNameBuilder.newInstance().add(UUID, uuid.toString()).build();
    }


    public UUID getUuid() {
        return uuid;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(final String email) {
        this.email = email;
    }


    public String getCreated() {
        return created;
    }


    public String getModified() {
        return modified;
    }


    public void setPassword(final String password) {
        this.password = password.getBytes(Charset.forName("UTF-8"));
    }

    public void setUserActive(boolean active) {
        this.userStatus = active ? USER_STATUS_ACTIVE : USER_STATUS_INACTIVE;
    }

    public void prepareForSaving() {
        created = null;
        modified = null;
    }


    @Override
    public String toString() {
        return "Identity{" +
                "dn=" + dn +
                ", uuid=" + uuid +
                ", email='" + email + '\'' +
                ", password=" + Arrays.toString(password) +
                ", created='" + created + '\'' +
                ", modified='" + modified + '\'' +
                ", sn='" + sn + '\'' +
                ", cn='" + cn + '\'' +
                ", userStatus='" + userStatus + '\'' +
                '}';
    }

    public static class Constants {

        public static final String UUID = "uid";
        public static final String EMAIL = "mail";
        public static final String PASSWORD = "userPassword";
        public static final String CREATED = "createTimestamp";
        public static final String MODIFIED = "modifyTimestamp";
        public static final String USER_STATUS = "employeeType";    // eeewwwwwww

        // this is what's actually set into the LDAP employeeType attribute to indicate that the user is active in the system -
        // e.g. their email has been validated
        static final String USER_STATUS_ACTIVE = "active";
        static final String USER_STATUS_INACTIVE = "inactive";

        public static final String[] ATTRIBUTES = {
            UUID,
            EMAIL,
            PASSWORD,
            CREATED,
            MODIFIED,
            USER_STATUS,
        };
    }
}
