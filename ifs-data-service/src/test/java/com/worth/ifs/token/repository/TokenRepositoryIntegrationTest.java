package com.worth.ifs.token.repository;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static com.worth.ifs.token.resource.TokenType.RESET_PASSWORD;
import static com.worth.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<TokenRepository> {

    private static final CharSequence HASH_SALT = "klj12nm6nsdgfnlk12ctw476kl";

    @Before
    public void setUp() throws Exception {

    }

    @Autowired
    @Override
    protected void setRepository(final TokenRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findByHash() throws Exception {
        final String hash = "5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0";

        final Token token = repository.findByHash(hash).get();
        assertEquals(hash, token.getHash());
        assertEquals(User.class.getName(), token.getClassName());
        assertEquals(Long.valueOf(11L), token.getClassPk());
        assertEquals(VERIFY_EMAIL_ADDRESS, token.getType());
        assertEquals(JsonNodeFactory.instance.objectNode().put("competitionId", 1), token.getExtraInfo());

    }

    @Rollback
    @Test
    public void test_findByHashAndTypeAndClassName() throws Exception {
        final String hash1 = hash(1L, "firstname.lastname@innovateuk.org");
        final String hash2 = hash(1L, "firstname.lastname@innovateuk.org");

        final Token token1 = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash1, now(), JsonNodeFactory.instance.objectNode());
        final Token token2 = new Token(RESET_PASSWORD, User.class.getName(), 1L, hash2, now(), JsonNodeFactory.instance.objectNode());

        final Token expected = repository.save(token1);
        repository.save(token2);

        final Optional<Token> found = repository.findByHashAndTypeAndClassName(hash1, VERIFY_EMAIL_ADDRESS, User.class.getName());
        assertTrue(found.isPresent());
        assertEquals(expected, found.get());
    }

    private String hash(final Long userId, final String email) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);
        int random = (int) Math.ceil(Math.random() * 1000); // random number from 1 to 1000
        String hash = String.format("%s==%s==%s", userId, email, random);
        return encoder.encode(hash);
    }

}