package org.innovateuk.ifs.sil.grant.resource.json;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

class GrantConstants {
    private GrantConstants() {
    }
    static final ZoneId GMT = ZoneId.of("GMT");
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(GMT);
}
