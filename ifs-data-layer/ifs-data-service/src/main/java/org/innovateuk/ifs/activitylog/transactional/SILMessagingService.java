
package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.sil.SIlPayloadKeyType;
import org.innovateuk.ifs.sil.SIlPayloadType;
import org.springframework.http.HttpStatus;

public interface SILMessagingService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordSilMessage(SIlPayloadType sIlPayloadType, SIlPayloadKeyType sIlPayloadKeyType,
                          String key, String payload, HttpStatus httpStatus);


}
