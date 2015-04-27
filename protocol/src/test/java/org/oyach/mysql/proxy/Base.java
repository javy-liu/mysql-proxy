package org.oyach.mysql.proxy;

/**
 * 扩展接口
 *
 * @author oyach
 * @since 0.0.1
 */
interface Base {
    void init(Engine context) throws Exception;

    void readHandshake(Engine context) throws Exception;

    void sendHandshake(Engine context) throws Exception;

    void readAuth(Engine context) throws Exception;

    void sendAuth(Engine context) throws Exception;

    void readAuthResult(Engine context) throws Exception;

    void sendAuthResult(Engine context) throws Exception;

    void readQuery(Engine context) throws Exception;

    void sendQuery(Engine context) throws Exception;

    void readQueryResult(Engine context) throws Exception;

    void sendQueryResult(Engine context) throws Exception;

    void cleanUp(Engine context) throws Exception;
}
