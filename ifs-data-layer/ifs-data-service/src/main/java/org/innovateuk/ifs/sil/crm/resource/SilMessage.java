package org.innovateuk.ifs.sil.crm.resource;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.innovateuk.ifs.sil.SilPayloadKeyType;
import org.innovateuk.ifs.sil.SilPayloadType;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Builder
@Entity
public class SilMessage {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column( updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;


    @Enumerated(EnumType.STRING)
    private SilPayloadType payloadType;

    @Enumerated(EnumType.STRING)
    private SilPayloadKeyType keyType;

    @Column(nullable = false)
    private String keyValue;

    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private String responseCode;


}
