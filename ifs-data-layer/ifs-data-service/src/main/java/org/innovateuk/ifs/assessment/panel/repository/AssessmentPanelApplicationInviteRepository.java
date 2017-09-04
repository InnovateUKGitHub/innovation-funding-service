package org.innovateuk.ifs.assessment.panel.repository;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentPanelApplicationInvite;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentPanelApplicationInviteRepository extends ProcessRepository<AssessmentPanelApplicationInvite>, PagingAndSortingRepository<AssessmentPanelApplicationInvite, Long> {


    List <AssessmentPanelApplicationInvite> findByActivityStateState(State state);

    List<AssessmentPanelApplicationInvite> findByActivityStateStateIn(Collection<State> states);


}
