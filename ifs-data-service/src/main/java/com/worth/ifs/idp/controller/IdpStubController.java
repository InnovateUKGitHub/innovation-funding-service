package com.worth.ifs.idp.controller;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Name;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.worth.ifs.util.JsonStatusResponse.created;
import static com.worth.ifs.util.JsonStatusResponse.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Stub for talking directly to LDAP in lieu of having a REST API available to update LDAP information
 */
@RestController
@RequestMapping("/idpstub")
public class IdpStubController {

    @Autowired
    private LdapTemplate ldapTemplate;

    @RequestMapping(value = "/createuser", method = POST, produces = "application/json")
    public JsonStatusResponse createUser(@RequestBody CreateUserResource createUserRequest, HttpServletResponse response) {
        String uid = UUID.randomUUID().toString();
        create(createUserRequest, uid);
        return created(uid, response);
    }

    @RequestMapping(value = "/updateuser", method = PUT, produces = "application/json")
    public JsonStatusResponse updateUser(@RequestBody UpdateUserResource updateUserRequest) {
        update(updateUserRequest);
        return ok();
    }

    public void create(CreateUserResource user, String uid) {
        Name dn = buildDn(uid);
        ldapTemplate.bind(dn, null, buildAttributes(user, uid));
    }

    private void update(UpdateUserResource updateUserRequest) {

        Name dn = buildDn(updateUserRequest.getUid());

        ModificationItem title = getModificationItem("title", updateUserRequest.getTitle());
        ModificationItem firstName = getModificationItem("givenName", updateUserRequest.getFirstName());
        ModificationItem lastName = getModificationItem("sn", updateUserRequest.getLastName());
        ModificationItem displayName = getModificationItem("displayName", updateUserRequest.getFirstName() + " " + updateUserRequest.getLastName());
        ModificationItem cn = getModificationItem("cn", updateUserRequest.getFirstName() + " " + updateUserRequest.getLastName());
        ModificationItem mail = getModificationItem("mail", updateUserRequest.getEmailAddress());
        ModificationItem telephoneNumber = getModificationItem("telephoneNumber", updateUserRequest.getPhoneNumber());

        ldapTemplate.modifyAttributes(dn, new ModificationItem[] {title, firstName, lastName, displayName, cn, mail, telephoneNumber});
    }

    private ModificationItem getModificationItem(String attributeName, String value) {
        Attribute attr = new BasicAttribute(attributeName, value);
        return new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
    }

    private Attributes buildAttributes(CreateUserResource user, String uid) {

        Attributes attrs = new BasicAttributes();
        BasicAttribute ocattr = new BasicAttribute("objectclass");
        ocattr.add("top");
        ocattr.add("person");
        ocattr.add("inetOrgPerson");
        attrs.put(ocattr);
        attrs.put("uid", uid);
        attrs.put("cn", user.getFirstName() + " " + user.getLastName());
        attrs.put("displayName", user.getFirstName() + " " + user.getLastName());
        attrs.put("givenName", user.getFirstName());
        attrs.put("sn", user.getLastName());
        attrs.put("mail", user.getEmailAddress());
        attrs.put("title", user.getTitle());
        attrs.put("userPassword", user.getPassword());
        attrs.put("telephoneNumber", user.getPhoneNumber());
        return attrs;
    }

    protected Name buildDn(String uid) {
        return LdapNameBuilder.newInstance()
                .add("uid", uid)
                .build();
    }
}
