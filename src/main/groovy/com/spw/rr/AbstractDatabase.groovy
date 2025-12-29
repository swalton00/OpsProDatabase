package com.spw.rr

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractDatabase {
    static DatabaseProcess db = null
    private static final Logger log = LoggerFactory.getLogger(com.spw.rr.AbstractDatabase.class)

    SqlSessionFactory sqlSessionFactory = null
    static SqlSession session = null


    /**
     * Initialize the Mybatis environment
     * @param url
     * @param userid
     * @param password
     */
    void initialize(String url, String schema, String userid, String password) {op
        log.debug("AbstractDatabase initializing")
        String resource = "mybatis-config.xml";
        Properties props = new Properties()
        props.put("url", url + ";SCHEMA=" + schema)
        props.put("username", userid)
        props.put("password", password)
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props );
        //session = sqlSessionFactory.openSession(org.apache.ibatis.session.ExecutorType.REUSE, true)
    }

    void endRun() {
        log.debug("ending the run -- closing the connection")
        if (session != null) {
            session.close()
        }

    }

    SqlSession getSession() {
        log.debug("getting a SqlSession")
        return sqlSessionFactory.openSession(true)
    }
}
