package com.zhm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by 赵红明 on 2019/7/15.
 */
public class AuthorityUtils {
    private final static Logger logger = LoggerFactory.getLogger(AuthorityUtils.class);


    private static final String SESSION_NAME = "logined_user_session_name";


    @SuppressWarnings({"unused"})
    public static LoginUser getCurrentUser() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            LoginUser currentUser = (LoginUser) requestAttributes.getAttribute(SESSION_NAME, RequestAttributes.SCOPE_REQUEST);
            return currentUser;
        } catch (IllegalStateException e) {
            return null;
        }

    }

    public static void setCurrentUser(LoginUser loginedUser) {
        logger.debug("setCurrentUser loginedUser : " + loginedUser.toString());
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        requestAttributes.setAttribute(SESSION_NAME, loginedUser, RequestAttributes.SCOPE_REQUEST);
    }
    public static void removeCurrentUser() {
        logger.debug("removeCurrentUser");
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        requestAttributes.removeAttribute(SESSION_NAME, RequestAttributes.SCOPE_REQUEST);
    }
}
