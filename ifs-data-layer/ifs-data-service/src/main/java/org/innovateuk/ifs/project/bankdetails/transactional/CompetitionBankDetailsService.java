package org.innovateuk.ifs.project.bankdetails.transactional;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

public interface CompetitionBankDetailsService {
    ByteArrayResource csvBankDetails(long competitionId) throws IOException;
}
