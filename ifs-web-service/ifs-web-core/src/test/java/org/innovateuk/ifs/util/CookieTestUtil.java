package org.innovateuk.ifs.util;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;

/**
 * Helper around stubbing cookie encryption.
 */
public class CookieTestUtil {
    private static String password = "mysecretpassword";
    private static String salt = "109240124012412412";
    public static TextEncryptor encryptor = Encryptors.text(password, salt);

    public static void setupEncryptedCookieService(EncryptedCookieService cookieUtil) {
        ReflectionTestUtils.setField(cookieUtil, "cookieSecure", TRUE);
        ReflectionTestUtils.setField(cookieUtil, "cookieHttpOnly", FALSE);
        ReflectionTestUtils.setField(cookieUtil, "encryptionPassword", password);
        ReflectionTestUtils.setField(cookieUtil, "encryptionSalt", salt);
        ReflectionTestUtils.setField(cookieUtil, "encryptor", encryptor);

        doCallRealMethod().when(cookieUtil).saveToCookie(any(HttpServletResponse.class), any(String.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookie(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookieValue(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).removeCookie(any(HttpServletResponse.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookieAs(any(HttpServletRequest.class), any(String.class), any());
        doCallRealMethod().when(cookieUtil).getCookieAsList(any(HttpServletRequest.class), any(String.class), any());
        doCallRealMethod().when(cookieUtil).getValueFromCookie(any(String.class));
        doCallRealMethod().when(cookieUtil).getValueToSave(any(String.class));
        doCallRealMethod().when(cookieUtil).decodeValue(any(String.class));
        doCallRealMethod().when(cookieUtil).encodeValue(any(String.class));
    }

    public static void setupCompressedCookieService(CompressedCookieService cookieUtil) {
        ReflectionTestUtils.setField(cookieUtil, "cookieSecure", TRUE);
        ReflectionTestUtils.setField(cookieUtil, "cookieHttpOnly", FALSE);

        doCallRealMethod().when(cookieUtil).getCookie(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).saveToCookie(any(HttpServletResponse.class), any(String.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getCookieValue(any(HttpServletRequest.class), any(String.class));
        doCallRealMethod().when(cookieUtil).getValueFromCookie(any(String.class));
        doCallRealMethod().when(cookieUtil).getValueToSave(any(String.class));
    }

    public static String getDecryptedCookieValue(Cookie[] cookies, String cookieName) {
        Optional<Cookie> cookieFound = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny();

        return cookieFound.map(cookie -> encryptor.decrypt(cookie.getValue())).orElse(null);

    }
}
