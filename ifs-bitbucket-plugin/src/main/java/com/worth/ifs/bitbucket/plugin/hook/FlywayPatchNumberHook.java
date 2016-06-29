package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.ContentService;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheck;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheckContext;
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
        context.getMergeRequest().veto("Deny All", "Deny All");
    }
}
