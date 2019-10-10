package org.innovateuk.ifs.assessment.dashboard.repository;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.data.repository.query.Param;

public interface ApplicationAssessmentRepository extends AssessmentRepository {

//    String APPLICATION_ID = "SELECT a.id, p.id, c.name FROM Process p" +
//            "INNER JOIN Application a ON a.id = p.target_id" +
//            "INNER JOIN Competition c on c.id = a.competition" +
//            "WHERE p.process_type = 'Assessment' AND p.target_id = :applicationId";
//



}
