package com.worth.ifs.user.resource;

import com.worth.ifs.commons.resource.ResourceEnvelope;

import javax.annotation.Resource;

public class UserResourceEnvelope extends ResourceEnvelope<UserResource> {
    public UserResourceEnvelope() {}
    public UserResourceEnvelope(ResourceEnvelope<UserResource> userResourceEnvelope) {
        super(userResourceEnvelope);
    }
}
