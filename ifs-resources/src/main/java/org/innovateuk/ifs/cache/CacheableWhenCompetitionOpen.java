package org.innovateuk.ifs.cache;


import java.io.Serializable;
import java.util.Collection;


public interface CacheableWhenCompetitionOpen extends Serializable {

    boolean isCompetitionOpen();
}
