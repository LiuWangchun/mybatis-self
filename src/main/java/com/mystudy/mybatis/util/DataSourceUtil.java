package com.mystudy.mybatis.util;

import com.mystudy.mybatis.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *  <p>Title: DataSourceUtil</p>
 * * <p>Description: 数据源的工具类</p>
 */
public class DataSourceUtil {

    public static Connection getConnection(Configuration cfg) {
        try {
            Class.forName(cfg.getDriver());
            Connection conn = DriverManager.getConnection(cfg.getUrl(),cfg.getUsername(),cfg.getPassword());
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
