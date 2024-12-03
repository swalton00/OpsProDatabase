package com.spw.rr

import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.io.Resources

@Singleton
class DatabaseProcess {

    void intialize(String url, String userid, String password) {
        String resource = "mybatis-config.xml";
        Properties props = new Properties()
        props.put("url", url)
        props.put("username", userid)
        props.put("password", password)
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream, );

    }
}
