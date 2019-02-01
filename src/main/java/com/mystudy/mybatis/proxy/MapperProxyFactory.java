package com.mystudy.mybatis.proxy;

import com.mystudy.mybatis.config.Mapper;
import com.mystudy.mybatis.executor.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

/**
 * <p>Title: MapperProxyFactory</p>
 * <p>Description: 用于创建代理对象是增强方法</p>
 */
public class MapperProxyFactory implements InvocationHandler {

    private Map<String, Mapper> mappers;
    private Connection conn;

    public MapperProxyFactory(Map<String, Mapper> mappers, Connection conn) {
        this.mappers = mappers;
        this.conn = conn;
    }

    /**
     * 对当前正在执行的方法进行增强
     * 取出当前执行的方法名称
     * 取出当前执行的方法所在类
     * 拼接成 key
     * 去 Map 中获取 Value（Mapper)
     * 使用工具类 Executor 的 selectList 方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.取出方法名
        String methodName = method.getName();
        //2.取出方法所在类名
        String className = method.getDeclaringClass().getName();
        //拼接成key
        String key = className+"."+methodName;
        //根据key取出Mapper
        Mapper mapper = mappers.get(key);
        if(mapper != null) {
            throw new IllegalArgumentException("传入的参数有误，无法获取执行的必要条件");
        }
        //创建Executor对象
        Executor executor = new Executor();
        return executor.selectList(mapper, conn);
    }
}
