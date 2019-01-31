package com.mystudy.mybatis.config;

/**
 * 用于封装查询时的必要信息：要执行的SQL语句和返回结果类型的全限定名
 */
public class Mapper {

    private String queryString;//sql
    private String resultType;//结果类型的全限定类名

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
