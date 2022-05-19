
package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;
import org.springframework.http.HttpStatus;

public interface SilMessageRecordingService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void recordSilMessage(SilPayloadType sIlPayloadType, SilPayloadKeyType sIlPayloadKeyType,
                          String key, String payload, HttpStatus httpStatus);


}
