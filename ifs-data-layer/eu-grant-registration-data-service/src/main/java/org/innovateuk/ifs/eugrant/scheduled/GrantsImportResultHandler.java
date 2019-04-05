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

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simplePartition;

/**
 * A component to record the success or failure of an import run.
 *
 * This is useful to have for testing purposes as well as logging purposes as the Spring documentation
 * discourages the use of non-void returns in @Scheduled methods, and so we are able to verify the results of
 * {@link ScheduledEuGrantFileImporter#importEuGrantsFile()} by using this component during tests.
 */
@Component
public class GrantsImportResultHandler {

    private static final Log LOG = LogFactory.getLog(GrantsImportResultHandler.class);

    void recordResult(ServiceResult<Pair<File, List<ServiceResult<EuGrantResource>>>> result, File sourceFile) {
        result.handleSuccessOrFailureNoReturn(failure -> logFailure(failure, sourceFile),
                success -> logSuccess(success, sourceFile));
    }

    private void logSuccess(Pair<File, List<ServiceResult<EuGrantResource>>> success, File sourceFile) {
        List<ServiceResult<EuGrantResource>> importResults = success.getRight();
        Pair<List<ServiceResult<EuGrantResource>>, List<ServiceResult<EuGrantResource>>> successesAndFailures =
                simplePartition(importResults, ServiceResult::isSuccess);
        int importSuccessCount = successesAndFailures.getLeft().size();
        int importFailureCount = successesAndFailures.getRight().size();

        logBar();
        LOG.info("Results of " + sourceFile.getName());

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
        logBar();
    }

    private void logFailure(ServiceFailure failure, File sourceFile) {
        logBar();
        LOG.error(format("Unable to complete import of %s. Failure is: %s", sourceFile.getName(), failure));
        logBar();
    }

    private void logBar() {
        LOG.info("--------------------------------------------------------");
    }
}
