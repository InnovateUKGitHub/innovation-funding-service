package com.worth.ifs.idp.controller;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Stub for talking directly to LDAP in lieu of having a REST API available to update LDAP information
 */
@RestController
@RequestMapping("/idpstub")
public class IdpStubController {

    @Autowired
    private LdapTemplate ldapTemplate;

    @RequestMapping(value = "/createuser", method = POST, produces = "application/json")
    public JsonStatusResponse createUser(@RequestBody CreateUserResource createUserRequest) {
        String uid = UUID.randomUUID().toString();
        create(createUserRequest, uid);
        return JsonStatusResponse.ok(uid);
    }


    public void create(CreateUserResource user, String uid) {
        Name dn = buildDn(uid);
        ldapTemplate.bind(dn, null, buildAttributes(user, uid));
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
