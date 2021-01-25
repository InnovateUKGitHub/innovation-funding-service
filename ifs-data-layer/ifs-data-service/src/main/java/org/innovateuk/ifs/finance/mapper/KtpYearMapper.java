package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.KtpFinancialYear;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class KtpYearMapper extends BaseResourceMapper<KtpFinancialYear, KtpYearResource> {
}
