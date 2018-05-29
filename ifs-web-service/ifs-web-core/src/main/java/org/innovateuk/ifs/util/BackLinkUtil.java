package org.innovateuk.ifs.util;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Util class to help build 'Back' button links.
 */
public final class BackLinkUtil {

    private BackLinkUtil() {}

    /**
     * Builds a URI query string that can be used to return a user to an original location
     * (i.e. through clicking a Back button).
     *
     * Primarily used to remove boilerplate code and ensures that there is an {@code origin} parameter.
     *
     * @param origin is the enum representation of the origin URL. This is mapped
     *               to the {@code origin} parameter of the query string as the enum string,
     *               for example: {@code ?param=1&origin=ALL_APPLICATIONS}.
     * @param queryParams provided from Spring.
     * @return a UTF-8 encoded query string with at least an {@code origin} parameter.
     */
    public static String buildOriginQueryString(Enum<?> origin, MultiValueMap<String, String> queryParams) {
        queryParams.remove("origin");

        return UriComponentsBuilder.newInstance()
                .queryParam("origin", origin.toString())
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
    }
}
