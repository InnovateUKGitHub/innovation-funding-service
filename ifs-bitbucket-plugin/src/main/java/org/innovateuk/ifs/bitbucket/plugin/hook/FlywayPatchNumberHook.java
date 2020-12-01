package org.innovateuk.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.ContentService;
import com.atlassian.bitbucket.content.ContentTreeCallback;
import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A merge hook to ensure Flyway patches can't get out of order.
 */
@Scanned
public class FlywayPatchNumberHook implements RepositoryMergeCheck {

    private final ContentService cs;

    @Autowired
    public FlywayPatchNumberHook(@ComponentImport final ContentService cs) {
        this.cs = cs;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context, @Nonnull PullRequestMergeHookRequest mergeHookRequest) {
        // To
        final PullRequestRef toRef = mergeHookRequest.getPullRequest().getToRef();
        final String toLastCommitId = toRef.getLatestCommit();
        final Repository toRepo = toRef.getRepository();
        // From
        final PullRequestRef fromRef = mergeHookRequest.getPullRequest().getFromRef();
        final String fromLastCommitId = fromRef.getLatestCommit();
        final Repository fromRepo = fromRef.getRepository();

        List<String> errors = new ArrayList<>();
        errors.addAll(errors("/migration", toRepo, fromRepo, toLastCommitId, fromLastCommitId));
        errors.addAll(errors("/reference", toRepo, fromRepo, toLastCommitId, fromLastCommitId));
        errors.addAll(errors("/setup", toRepo, fromRepo, toLastCommitId, fromLastCommitId));

        if (errors.isEmpty()) {
            return RepositoryHookResult.accepted();
        } else {
            String message = String.join("/n", errors);
            return RepositoryHookResult.rejected(message, message);
        }
    }

    private List<String> errors(String directory, Repository toRepo, Repository fromRepo, String toLastCommitId, String fromLastCommitId) {
        final FlywayToFromVersionCallBack flywayToFromVersionCallBack = new FlywayToFromVersionCallBack();

        final ContentTreeCallback toCallBack = new FlywayVersionContentTreeCallback(flywayToFromVersionCallBack::onTo);
        final ContentTreeCallback fromCallBack = new FlywayVersionContentTreeCallback(flywayToFromVersionCallBack::onFrom);
        final PageRequest pageRequest = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT);

        cs.streamDirectory(toRepo, toLastCommitId, "ifs-data-layer/ifs-data-service/src/main/resources/db" + directory, true, toCallBack, pageRequest);
        cs.streamDirectory(fromRepo, fromLastCommitId, "ifs-data-layer/ifs-data-service/src/main/resources/db" + directory, true, fromCallBack, pageRequest);

        return flywayToFromVersionCallBack.getErrors();
    }
}
