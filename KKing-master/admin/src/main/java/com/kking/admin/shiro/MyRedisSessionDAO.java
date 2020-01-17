package com.kking.admin.shiro;

import com.kking.admin.util.RedisCacheManager;
import com.kking.dao.entity.UserToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

@Service
public class MyRedisSessionDAO extends AbstractSessionDAO {
    Log log = LogFactory.getLog(MyRedisSessionDAO.class);
    private final String REDIS_HASH = "session";

    private static final String TOKEN_KEY = "user:";

    @Autowired
    RedisCacheManager redisCacheManager;


    @Override
    protected Serializable doCreate(Session session) {
        Serializable id = super.generateSessionId(session);
        ((SimpleSession)session).setId(id);
        log.debug("创建session:" + id);
        redisCacheManager.hset(REDIS_HASH,id.toString(),session);
        return id;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = (Session)redisCacheManager.hget(REDIS_HASH,sessionId.toString());
        log.debug("读取session:" + sessionId);
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        Serializable id = session.getId();
        if(id == null){
            throw new NullPointerException();
        }
        redisCacheManager.hset(REDIS_HASH,id.toString(),session);

    }

    @Override
    public void delete(Session session) {
        Serializable id = session.getId();
        redisCacheManager.hdel(REDIS_HASH,id.toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return redisCacheManager.hgetAllValues(REDIS_HASH);
    }

    public boolean setToken(UserToken userToken){
        try {
            redisCacheManager.hset(TOKEN_KEY+userToken.getToken(),"userId",userToken.getUserId());
            redisCacheManager.hset(TOKEN_KEY+userToken.getToken(),"userName",userToken.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public UserToken getToken(String session){
        UserToken userToken=new UserToken();
        userToken.setToken(session);
        try {
            Integer userId= (Integer) redisCacheManager.hget(TOKEN_KEY+session,"userId");
            String userName= String.valueOf(redisCacheManager.hget(TOKEN_KEY+session,"userName"));
            userToken.setUserName(userName);
            userToken.setUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return userToken;
    }
}
