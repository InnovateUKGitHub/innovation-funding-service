package org.innovateuk.ifs.sil.common.json;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final ZoneId GMT = ZoneId.of("GMT");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(GMT);
    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final DateTimeFormatter LOANS_DATETIME_FORMAT = DateTimeFormatter.ISO_INSTANT;
}
