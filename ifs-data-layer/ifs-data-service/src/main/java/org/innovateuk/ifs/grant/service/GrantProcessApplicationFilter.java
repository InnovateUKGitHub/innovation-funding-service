package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.sil.grant.resource.Grant;

public interface GrantProcessApplicationFilter {
    boolean shouldSend(Grant grant);

    String generateFilterReason(Grant grant);
}
