package com.mystudy.mybatis.builder;

import com.mystudy.mybatis.config.Configuration;
import com.mystudy.mybatis.util.DataSourceUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * <p>Title: DefaultSqlSession</p>
 * <p>Description: SqlSession 的具体实现</p>
 */
public class DefaultSqlSession implements SqlSession {
    //核心配置对象
    private Configuration cfg;

    public void setCfg(Configuration cfg) {
        this.cfg = cfg;
    }

    //连接对象
    private Connection conn;

    //调用DataSourceUtil工具类获取连接
    public Connection getConn() {
        //TODO
        return DataSourceUtil.getConnection(cfg);
    }


    /**
     * 动态代理：
     * 涉及的类：Proxy
     * 使用的方法：newProxyInstance
     * 方法的参数：
     * ClassLoader：和被代理对象使用相同的类加载器,通常都是固定的
     * Class[]：代理对象和被代理对象要求有相同的行为。（具有相同的方法）
     * InvocationHandler：如何代理。需要我们自己提供的增强部分的代码
     */

    public <T> T getMapper(Class<T> daoClass) {
        conn = getConn();
        System.out.println(conn);
        T daoProxy = Proxy.newProxyInstance(daoClass.getClassLoader(),)
        return null;
    }

    public void close() {

    }
}
