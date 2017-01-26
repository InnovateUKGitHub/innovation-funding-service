package org.innovateuk.ifs.publiccontent.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.mapstruct.Mapper;

/**
 * Created by luke.harper on 25/01/2017.
 */
@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class KeywordMapper {

    public String keywordToString(Keyword keyword) {
        return keyword.getKeyword();
    }

    public Keyword stringToKeyword(String keywordText) {
        Keyword keyword = new Keyword();
        keyword.setKeyword(keywordText);
        return keyword;
    }
}
