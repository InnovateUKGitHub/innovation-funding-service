package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

public interface CompetitionBankDetailsService {

    @NotSecured(value = "Service should only be calling other services to receive data and should be using their permission rules.", mustBeSecuredByOtherServices = false)
    ByteArrayResource csvBankDetails(long competitionId) throws IOException;
}
