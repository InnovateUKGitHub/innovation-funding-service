package org.innovateuk.ifs.workflow.repository;

import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * A repository handling Activity State retrieval
 */
public interface ActivityStateRepository extends PagingAndSortingRepository<ActivityState, Long> {

    ActivityState findOneByActivityTypeAndState(ActivityType activityType, State state);
}
