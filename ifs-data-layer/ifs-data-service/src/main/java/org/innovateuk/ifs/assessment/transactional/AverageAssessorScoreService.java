package org.innovateuk.ifs.assessment.transactional;

import java.math.BigDecimal;

public interface AverageAssessorScoreService {

    BigDecimal calculateAverageAssessorScore(long applicationId);
    
}
