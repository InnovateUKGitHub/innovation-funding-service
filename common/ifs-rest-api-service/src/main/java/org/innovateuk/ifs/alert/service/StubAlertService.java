package org.innovateuk.ifs.alert.service;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@Profile(IfsProfileConstants.STUBDEV)
public class StubAlertService implements AlertRestService {

    @Override
    public RestResult<List<AlertResource>> findAllVisible() {
        return RestResult.restSuccess(ImmutableList.of(
                new AlertResource(1L, "Stub Mode", AlertType.MAINTENANCE,
                        ZonedDateTime.now().minusDays(1L), ZonedDateTime.now().plusDays(1L)))
        );
    }

    @Override
    public RestResult<List<AlertResource>> findAllVisibleByType(AlertType type) {
        return RestResult.restSuccess(ImmutableList.of(
                new AlertResource(1L, "Stub Mode", AlertType.MAINTENANCE,
                        ZonedDateTime.now().minusDays(1L), ZonedDateTime.now().plusDays(1L)))
        );
    }

    @Override
    public RestResult<AlertResource> getAlertById(Long id) {
        return RestResult.restSuccess(new AlertResource());
    }

    @Override
    public RestResult<AlertResource> create(AlertResource alertResource) {
        return RestResult.restSuccess(new AlertResource());
    }

    @Override
    public RestResult<Void> delete(Long id) {
        return RestResult.restSuccess();
    }

    @Override
    public RestResult<Void> deleteAllByType(AlertType type) {
        return RestResult.restSuccess();
    }
}

