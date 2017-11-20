package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;

public interface AssessmentPanelRestService {
    RestResult<Void> assignToPanel(long applicationId);
    RestResult<Void> unassignFromPanel(long applicationId);
}
