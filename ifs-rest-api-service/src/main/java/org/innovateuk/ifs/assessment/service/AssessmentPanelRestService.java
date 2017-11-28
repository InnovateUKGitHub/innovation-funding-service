package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

/**
 *  * REST service for managing applications on an assessment panel.
 */
public interface AssessmentPanelRestService {
    RestResult<Void> assignToPanel(long applicationId);
    RestResult<Void> unassignFromPanel(long applicationId);
}
