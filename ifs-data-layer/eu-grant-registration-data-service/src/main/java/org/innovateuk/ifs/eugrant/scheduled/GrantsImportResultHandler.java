package org.innovateuk.ifs.eugrant.scheduled;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simplePartition;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsImportResultHandler {

    private static final Log LOG = LogFactory.getLog(GrantsImportResultHandler.class);

    void recordResult(ServiceResult<Pair<File, List<ServiceResult<EuGrantResource>>>> result) {
        result.handleSuccessOrFailureNoReturn(this::logFailure, this::logSuccess);
    }

    private void logFailure(ServiceFailure failure) {
        LOG.error("Unable to complete grant file import.  Failure is: " + failure);
    }

    private void logSuccess(Pair<File, List<ServiceResult<EuGrantResource>>> success) {

        File resultsFile = success.getLeft();

        List<ServiceResult<EuGrantResource>> importResults = success.getRight();
        Pair<List<ServiceResult<EuGrantResource>>, List<ServiceResult<EuGrantResource>>> successesAndFailures =
                simplePartition(importResults, ServiceResult::isSuccess);
        int importSuccessCount = successesAndFailures.getLeft().size();
        int importFailureCount = successesAndFailures.getRight().size();

        LOG.info("Grants import complete.");

        if (importSuccessCount > 0) {
            LOG.info(importSuccessCount + " successful imports.");
        } else {
            LOG.warn("No successful imports.");
        }

        if (importFailureCount == 0) {
            LOG.info("No failed imports.");
        } else {
            LOG.warn(importFailureCount + " failed to import.");
        }

        LOG.info("Results file can be found at " + resultsFile.getPath());
    }
}
