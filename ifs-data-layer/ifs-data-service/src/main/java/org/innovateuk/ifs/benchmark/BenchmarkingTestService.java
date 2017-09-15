package org.innovateuk.ifs.benchmark;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service that can be used for benchmarking testing to access other layer of the application
 */
@Service
public class BenchmarkingTestService {
    
    @Autowired
    private PublicContentRepository publicContentRepository;

    @NotSecured(value = "A benchmarking helper method for doing internal benchmarking", mustBeSecuredByOtherServices = false)
    public void interactWithDatabase() {
        publicContentRepository.findByCompetitionId(1L);
    }
}
