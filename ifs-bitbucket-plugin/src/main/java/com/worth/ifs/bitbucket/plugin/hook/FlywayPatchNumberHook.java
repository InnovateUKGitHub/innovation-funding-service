package com.worth.ifs.bitbucket.plugin.hook;

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
import org.springframework.beans.factory.annotation.Autowired;

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

        final ContentTreeCallback toCallBack = new FlywayVersionContentTreeCallback(flywayToFromVersionCallBack::onTo);
        final ContentTreeCallback fromCallBack = new FlywayVersionContentTreeCallback(flywayToFromVersionCallBack::onFrom);

        cs.streamDirectory(toRepo, toLastCommitId, "", true, toCallBack, pageRequest);
        cs.streamDirectory(fromRepo, fromLastCommitId, "", true, fromCallBack, pageRequest);
        // Callbacks handle the rejection as required.
    }

}
