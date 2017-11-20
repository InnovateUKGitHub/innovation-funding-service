package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Created by tom on 16/11/2017.
 */
public interface AssessmentPanelService {

    ServiceResult<Void> assignApplicationToPanel(long applicationId);

    ServiceResult<Void> unassignApplicationFromPanel(long applicationId);

}
