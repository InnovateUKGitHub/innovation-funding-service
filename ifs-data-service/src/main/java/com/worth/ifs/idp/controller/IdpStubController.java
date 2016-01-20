package com.worth.ifs.idp.controller;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/idpstub/user")
public class IdpStubController {

    @Autowired
    private LdapTemplate ldapTemplate;

    @RequestMapping(method = POST, produces = "application/json")
    public JsonStatusResponse createUser(@RequestBody CreateUserResource createUserRequest, HttpServletResponse response) {
        String uid = UUID.randomUUID().toString();
        create(createUserRequest, uid);
        return created(uid, response);
    }

    @RequestMapping(value = "/{uid}", method = PUT, produces = "application/json")
    public JsonStatusResponse updateUser(@RequestBody UpdateUserResource updateUserRequest, @PathVariable("uid") String uid) {
        update(updateUserRequest, uid);
        return ok();
    }

    public void create(CreateUserResource user, String uid) {
        Name dn = buildDn(uid);
        ldapTemplate.bind(dn, null, buildAttributes(user, uid));
    }

    private void update(UpdateUserResource updateUserRequest, String uid) {

        Name dn = buildDn(uid);

        ModificationItem password = getModificationItem("userPassword", updateUserRequest.getEmailAddress());

        ldapTemplate.modifyAttributes(dn, new ModificationItem[] {password});
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
        attrs.put("mail", user.getEmailAddress());
        attrs.put("userPassword", user.getPassword());
        return attrs;
    }

    protected Name buildDn(String uid) {
        return LdapNameBuilder.newInstance()
                .add("uid", uid)
                .build();
    }
}
