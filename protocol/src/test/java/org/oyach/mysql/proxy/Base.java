package org.oyach.mysql.proxy;

/**
 * 扩展接口
 *
 * @author oyach
 * @since 0.0.1
 */
interface Base {
    void init() throws Exception;

    void readHandshake() throws Exception;

    void sendHandshake();

    void readAuth();

    void sendAuth();

    void readAuthResult();

    void sendAuthResult();

    void readQuery();

    void sendQuery();

    void readQueryResult();

    void sendQueryResult();

    void cleanUp();
}
