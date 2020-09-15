package org.innovateuk.ifs.form.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class MultipleChoiceOptionMapper extends BaseResourceMapper<MultipleChoiceOption, MultipleChoiceOptionResource> {

}
