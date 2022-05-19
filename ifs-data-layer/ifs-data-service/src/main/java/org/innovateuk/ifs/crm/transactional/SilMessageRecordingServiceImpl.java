
package org.innovateuk.ifs.crm.transactional;


import org.innovateuk.ifs.activitylog.repository.SilMessageRepository;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilMessage;
import org.innovateuk.ifs.util.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SilMessageRecordingServiceImpl implements SilMessageRecordingService {


    @Autowired
    SilMessageRepository silMessageRepository;


    @Override
    public void recordSilMessage(SilPayloadType payloadType, SilPayloadKeyType keyType,
                                 String key, String payload, HttpStatus httpStatus) {


        SilMessage silMessage = SilMessage.builder()
                .payloadType(payloadType)
                .keyType(keyType)
                .keyValue(key)
                .payload(payload)
                .responseCode(Optional.ofNullable(httpStatus).map(Enum::name).orElse(null))
                .dateCreated(TimeMachine.now())
                .build();

        silMessageRepository.save(silMessage);


    }
}



