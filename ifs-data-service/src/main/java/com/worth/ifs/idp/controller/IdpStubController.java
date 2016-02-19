package com.worth.ifs.idp.controller;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.CreateUserResponse;
import com.worth.ifs.authentication.resource.IdentityProviderError;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Name;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
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
    public ResponseEntity<?> createUser(@RequestBody CreateUserResource createUserRequest, HttpServletResponse response) {

        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "inetOrgPerson"));
        filter.and(new WhitespaceWildcardsFilter("mail", createUserRequest.getEmailAddress()));

        LdapQueryBuilder query = LdapQueryBuilder.query();
        query.filter(filter);

        List<String> results = ldapTemplate.search(query, (AttributesMapper<String>) Object::toString);

        if (!results.isEmpty()) {
            return new ResponseEntity<>(new IdentityProviderError("DUPLICATE_EMAIL_ADDRESS", emptyList()), CONFLICT);
        }

        String uid = UUID.randomUUID().toString();
        create(createUserRequest, uid);
        return new ResponseEntity<>(new CreateUserResponse(uid), CREATED);
    }

    @RequestMapping(value = "/{uid}", method = PUT, produces = "application/json")
    public RestResult<Void> updateUser(@RequestBody UpdateUserResource updateUserRequest, @PathVariable("uid") String uid) {
        update(updateUserRequest, uid);
        return RestResult.toPutResponse();
    }

    public void create(CreateUserResource user, String uid) {
        Name dn = buildDn(uid);
        ldapTemplate.bind(dn, null, buildAttributes(user, uid));
    }

    private void update(UpdateUserResource updateUserRequest, String uid) {

        Name dn = buildDn(uid);

        ModificationItem password = getModificationItem("userPassword", updateUserRequest.getPassword());

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
        attrs.put("cn", "unused");
        attrs.put("sn", "unused");
        attrs.put("mail", user.getEmailAddress());
        attrs.put("userPassword", user.getPlainTextPassword());
        return attrs;
    }

    protected Name buildDn(String uid) {
        return LdapNameBuilder.newInstance()
                .add("uid", uid)
                .build();
    }
}
