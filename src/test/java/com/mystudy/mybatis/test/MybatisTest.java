package com.mystudy.mybatis.test;

import com.mystudy.mybatis.builder.SqlSession;
import com.mystudy.mybatis.builder.SqlSessionFactory;
import com.mystudy.mybatis.builder.SqlSessionFactoryBuilder;
import com.mystudy.mybatis.dao.IUserDao;
import com.mystudy.mybatis.domain.User;
import com.mystudy.mybatis.resources.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    public static void main(String[] args) throws IOException {
        //1.读取配置文件
        InputStream in = Resources.getResourceAsStream("SqlMapConfig");
        //2.创建SqlSessionFactory的构建着对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //使用构建者创建工厂对象SqlSessionFactory
        SqlSessionFactory factory = builder.build(in);
        //4.使用 SqlSessionFactory 生产 SqlSession 对象
        SqlSession session = factory.openSession();
        //5.使用 SqlSession 创建 dao 接口的代理对象
        IUserDao userDao = session.getMapper(IUserDao.class);
        //6.使用代理对象执行查询所有方法
        List<User> users = userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        //7.释放资源
        session.close();
        in.close();
    }
}
