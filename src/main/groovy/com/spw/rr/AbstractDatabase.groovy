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
    SqlSession session = null


    void initialize(String mapper, String url, String userid, String password) {
        log.debug("AbstractDatabase initializing")
        String resource = "mybatis-config.xml";
        Properties props = new Properties()
        props.put("url", url)
        props.put("username", userid)
        props.put("password", password)
        props.put("mapper", mapper)
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props );
        session = sqlSessionFactory.openSession(org.apache.ibatis.session.ExecutorType.REUSE, true)
    }

    void endRun() {
        log.debug("ending the run -- closing the connection")
        session.close()
    }
}
