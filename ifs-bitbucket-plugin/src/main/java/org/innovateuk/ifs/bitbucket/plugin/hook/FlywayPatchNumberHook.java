package org.innovateuk.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.ContentService;
import com.atlassian.bitbucket.content.ContentTreeCallback;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheck;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheckContext;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * A merge hook to ensure Flyway patches can't get out of order.
 */
@Scanned
public class FlywayPatchNumberHook implements RepositoryMergeRequestCheck {

    private final ContentService cs;

    @Autowired
    public FlywayPatchNumberHook(@ComponentImport final ContentService cs) {
        this.cs = cs;
    }

    @Override
    public void check(final RepositoryMergeRequestCheckContext context) {
        // To
        final PullRequestRef toRef = context.getMergeRequest().getPullRequest().getToRef();
        final String toLastCommitId = toRef.getLatestCommit();
        final Repository toRepo = toRef.getRepository();
        // Form
        final PullRequestRef fromRef = context.getMergeRequest().getPullRequest().getFromRef();
        final String fromLastCommitId = fromRef.getLatestCommit();
        final Repository fromRepo = fromRef.getRepository();

        final PageRequest pageRequest = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT);
        final FlywayToFromVersionCallBack flywayToFromVersionCallBack = new FlywayToFromVersionCallBack(context.getMergeRequest());

        final ContentTreeCallback toCallBack = new FlywayVersionContentTreeCallback(new Consumer<List<Pair<String, List<Integer>>>>() {
            @Override
            public void accept(List<Pair<String, List<Integer>>> versions) {
                flywayToFromVersionCallBack.onTo(versions);
            }
        });
        final ContentTreeCallback fromCallBack = new FlywayVersionContentTreeCallback(new Consumer<List<Pair<String, List<Integer>>>>() {
            @Override
            public void accept(List<Pair<String, List<Integer>>> versions) {
                flywayToFromVersionCallBack.onFrom(versions);
            }
        });
        cs.streamDirectory(toRepo, toLastCommitId, "ifs-data-layer/ifs-data-service/src/main/resources/db", true, toCallBack, pageRequest);
        cs.streamDirectory(fromRepo, fromLastCommitId, "ifs-data-layer/ifs-data-service/src/main/resources/db", true, fromCallBack, pageRequest);
        // Callbacks handle the rejection as required.
    }

}
