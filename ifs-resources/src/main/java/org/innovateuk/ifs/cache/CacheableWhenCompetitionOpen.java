package org.innovateuk.ifs.cache;


import java.io.Serializable;


public interface CacheableWhenCompetitionOpen extends Serializable {

    boolean isCompetitionOpen();
}
