package org.innovateuk.ifs.docusign.api;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocusignApi {
    private static final Log LOG = LogFactory.getLog(DocusignApi.class);

    @Value("${ifs.docusign.api.base}")
    private String apiBasePath;
    @Value("${ifs.docusign.auth.base}")
    private String authBasePath;
    @Value("${ifs.docusign.auth.user}")
    private String userId;
    @Value("${ifs.docusign.auth.client}")
    private String clientId;
    @Value("${ifs.docusign.auth.key}")
    private String privateKey;

    private static final long TOKEN_EXPIRATION_IN_SECONDS = 3600;
    private static final long TOKEN_REPLACEMENT_IN_MILLISECONDS = 10 * 60 * 1000;

    private OAuth.OAuthToken oAuthToken;
    private long expiresIn;
    private ApiClient apiClient;

    public ApiClient getApiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient(apiBasePath);
        }
        try {
            checkToken();
            return apiClient;
        } catch (IOException | ApiException e) {
            throw new IFSRuntimeException(e);
        }
    }

    private void checkToken() throws IOException, ApiException {
        if(this.oAuthToken == null
                || (System.currentTimeMillis() + TOKEN_REPLACEMENT_IN_MILLISECONDS) > this.expiresIn) {
            updateToken();
        }
    }

    private void updateToken() throws IOException, ApiException {
        List<String> scopes = new ArrayList<>();
        // Only signature scope is needed. Impersonation scope is implied.
        scopes.add(OAuth.Scope_SIGNATURE);
        String privateKey = this.privateKey.replace("[newline]", "\n");
        byte[] privateKeyBytes = privateKey.getBytes();
        apiClient.setOAuthBasePath(authBasePath);

        OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(
                clientId,
                userId,
                scopes,
                privateKeyBytes,
                TOKEN_EXPIRATION_IN_SECONDS);
        apiClient.setAccessToken(oAuthToken.getAccessToken(), oAuthToken.getExpiresIn());

        expiresIn = System.currentTimeMillis() + (oAuthToken.getExpiresIn() * 1000);
    }
}
