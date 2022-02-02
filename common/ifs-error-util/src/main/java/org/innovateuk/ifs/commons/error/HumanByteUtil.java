package org.innovateuk.ifs.commons.error;

import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

public class HumanByteUtil {

    /**
     * Converts a byte count to human readable string for ranges we encounter (B, KB, MB)
     *
     * Uses consistent format of using no space e.g. 10MB
     *
     * @param byteCount the number of bytes
     * @return MB expression of bytes unless > 0 in which case KB
     */
    public static final String byteCountToHuman(Long byteCount) {
        DataSize dataSize = DataSize.of(byteCount, DataUnit.BYTES);
        if (dataSize.toMegabytes() == 0L) {
            if (dataSize.toKilobytes() == 0L) {
                return DataSize.of(byteCount, DataUnit.BYTES).toBytes() + "B";
            }
            return DataSize.of(byteCount, DataUnit.BYTES).toKilobytes() + "KB";
        }
        return DataSize.of(byteCount, DataUnit.BYTES).toMegabytes() + "MB";
    }

}
