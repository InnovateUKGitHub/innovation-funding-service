package org.innovateuk.ifs.schedule.transactional;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class SlackErrorReporter {
    private static final Log LOG = LogFactory.getLog(SlackErrorReporter.class);

    private SlackClient slackClient;

    private String channel;

    public SlackErrorReporter(@Value("${ifs.slack.auth.key:@null}") String authKey) {
        if (!isNullOrEmpty(authKey)) {
            SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
                    .setTokenSupplier(() -> authKey)
                    .build();
            this.slackClient = SlackClientFactory.defaultFactory().build(runtimeConfig);
            this.channel = "ifs_monitor_test";
        }
    }

    public void reportProblem(String problem) {
        if (slackClient != null) {
            slackClient.postMessage(
                    ChatPostMessageParams.builder()
                            .setText(problem)
                            .setChannelId(channel)
                            .build()
            ).join().ifErr(err -> LOG.error(err.getError()));
        }
    }
}
