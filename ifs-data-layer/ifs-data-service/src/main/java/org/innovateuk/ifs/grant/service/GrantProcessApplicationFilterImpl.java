package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GrantProcessApplicationFilterImpl implements GrantProcessApplicationFilter {
    private static final Log LOG = LogFactory.getLog(GrantProcessServiceImpl.class);

    private final boolean filterEnabled;
    private final List<Long> filterByCompetitionCodes;

    public GrantProcessApplicationFilterImpl(
            @Value("${ifs.grant.process.filterBy.competitionCodes}") final String filterByCompetitionCodesAsString
    ) {
        if (filterByCompetitionCodesAsString != null && !filterByCompetitionCodesAsString.trim().isEmpty()) {
            filterEnabled = true;
            filterByCompetitionCodes = Arrays
                    .stream(filterByCompetitionCodesAsString.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            LOG.info("Grant Monitoring : Only sending applications for competitions : " + filterByCompetitionCodes);
        } else {
            filterEnabled = false;
            filterByCompetitionCodes = Collections.emptyList();
            LOG.info("Grant Monitoring : All applications will be sent");
        }
    }

    @Override
    public boolean shouldSend(Grant grant) {
        return !filterEnabled || filterByCompetitionCodes.contains(grant.getCompetitionCode());
    }

    @Override
    public String generateFilterReason(Grant grant) {
        if (!shouldSend(grant)) {
            return "Competition code " + grant.getCompetitionCode() + " not in " + filterByCompetitionCodes;
        }
        return null;
    }
}
