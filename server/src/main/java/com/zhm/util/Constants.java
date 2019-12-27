package com.zhm.util;

/**
 * 参数
 */
public interface Constants {


    /**
     * 配置文件前缀
     */
    interface PropertiesPrefix {

        /**
         * 服务contextPath
         */
        String SERVER_CONTEXTPATH = "server.contextPath";
    }

    /**
     * 父参数节点
     */
    String PARENT_PARAMS = "parent.params";

    /**
     * 子参数节点
     */
    String SON_PARAMS = "son.params";

    String Login_auth="com.zhm.myModel";

    String address_local="com.zhm.address";

    interface Rediskey {
        // redis前缀
        String PREKEY = "MyModel.";

    }



}
