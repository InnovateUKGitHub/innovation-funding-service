package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.content.ContentService;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheck;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeRequestCheckContext;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.RepositorySettingsValidator;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

@Scanned
public class FlywayPatchNumberHook implements RepositoryMergeRequestCheck, RepositorySettingsValidator {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    private final ContentService cs;

    @Autowired
    public FlywayPatchNumberHook(@ComponentImport final ContentService cs) {
        this.cs = cs;
    }

    @Override
    public void check(final RepositoryMergeRequestCheckContext context) {
        int requiredReviewers = Integer.parseInt(context.getSettings().getString("reviewers"));
        int acceptedCount = 0;
        for (PullRequestParticipant reviewer : context.getMergeRequest().getPullRequest().getReviewers()) {
            acceptedCount = acceptedCount + (reviewer.isApproved() ? 1 : 0);
        }
        if (acceptedCount < requiredReviewers) {
            context.getMergeRequest().veto("Not enough approved reviewers", acceptedCount + " reviewers have approved your pull request. You need " + requiredReviewers + " (total) before you may merge.");
        }
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository) {
        String numReviewersString = settings.getString("reviewers", "0").trim();
        if (!NUMBER_PATTERN.matcher(numReviewersString).matches()) {
            errors.addFieldError("reviewers", "Enter a number");
        } else if (Integer.parseInt(numReviewersString) <= 0) {
            errors.addFieldError("reviewers", "Number of reviewers must be greater than zero");
        }
    }
}
