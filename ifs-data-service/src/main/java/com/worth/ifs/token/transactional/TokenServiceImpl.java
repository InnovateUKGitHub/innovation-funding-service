package com.worth.ifs.token.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TokenServiceImpl implements TokenService{
    private static final Log LOG = LogFactory.getLog(TokenServiceImpl.class);

    @Autowired
    TokenRepository repository;
    @Autowired
    ApplicationService applicationService;


    @Override
    public Optional<Token> getTokenByHash(String hash){
        return repository.findByHash(hash);
    }

    @Override
    public void removeToken(Token token){
        repository.delete(token);
    }

    /**
     *  if there are extra attributes in the token, then maybe we need to create a new application, or add the user to a application.
     */
    @Override
    public void handleExtraAttributes(Token token){
        JsonNode extraInfo = token.getExtraInfo();
        if(User.class.getName().equals(token.getClassName()) && extraInfo.has("competitionId")){
            Long competitionId = extraInfo.get("competitionId").asLong();
            if(competitionId != null && competitionId != 0L){
                applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, token.getClassPk(), "");
            }
        }
    }
}
