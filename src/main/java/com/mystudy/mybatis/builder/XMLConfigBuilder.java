package com.mystudy.mybatis.builder;

import com.mystudy.mybatis.config.Configuration;
import com.mystudy.mybatis.config.Mapper;
import com.mystudy.mybatis.resources.Resources;
import com.sun.org.apache.bcel.internal.generic.Select;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLConfigBuilder {

    /**
     * 解析主配置文件，把里面的内容填充到DefaultSqlSession所需要的地方
     * 使用的技术：
     * dom4j+xpath
     *
     * @param session
     * @param config
     */
    public static void loadConfiguration(DefaultSqlSession session, InputStream config) {
        try {
            //定义封转连接信息的配置对象
            Configuration cfg = new Configuration();

            //1.获取SAXReader对象
            SAXReader reader = new SAXReader();
            //2.根据字节输入流获取Document对象
            Document document = reader.read(config);
            //3.获取根节点
            Element root = document.getRootElement();
            //4.使用Xpath中选择指定节点的方式，获取所有property节点
            List<Element> propertyElements = root.selectNodes("//property");
            //遍历节点
            for (Element propertyElement : propertyElements) {
                //判断节点是连接数据库的哪部分信息
                //取出name属性的值
                String name = propertyElement.attributeValue("name");
                if ("driver".equals(name)) {
                    //表示驱动
                    //取出property标签value属性的值
                    String driver = propertyElement.attributeValue("value");
                    cfg.setDriver(driver);
                }
                if ("url".equals(name)) {
                    //表示URL字符串
                    //获取property标签value属性的值
                    String url = propertyElement.attributeValue("value");
                    cfg.setUrl(url);
                }
                if ("username".equals(name)) {
                    //表示用户名
                    //获取property标签value属性的值
                    String username = propertyElement.attributeValue("value");
                    cfg.setUsername(username);
                }
                if ("password".equals(name)) {
                    //表示密码
                    //获取property标签的value属性的值
                    String password = propertyElement.attributeValue("value");
                    cfg.setPassword(password);
                }
            }

            //取出mappers中的所有mapper标签，判断他们使用了resource属性还是class属性
            List<Element> mapperElements = root.selectNodes("//mapppers/mapper");
            //遍历集合
            for (Element mapperElement : mapperElements) {
                //判断mapperElement使用的是哪个属性
                Attribute attribute = mapperElement.attribute("resource");
                if (attribute != null) {
                    System.out.println("使用的是XML");
                    //表示有resource属性，用的是XML
                    //取出属性的值
                    String mapperPath = attribute.getValue();//获取属性的
                    // 值"com/mystudy/mybatis/dao/IUserDao.xml"
                    //把映射配置文件的内容获取出来，封装成一个map
                    Map<String, Mapper> mappers = loadMapperConfiguration(mapperPath);
                    //给configuration中的mappers赋值
                    cfg.setMappers(mappers);
                } /*else {
                    System.out.println("使用的是注解");
                    //表示没有resource属性，用的是注解
                    //获取class属性的值
                    String daoClassPath = mapperElement.attributeValue("class");
                    //根据daoClassPath获取封装的必要信息
                    Map<String, Mapper> mappers = loadMapperAnnotation(daoClassPath);
                    //给configuration中的mappers赋值
                    cfg.setMappers(mappers);
                }*/
            }

            //把配置对象传递给DefaultSqlSession
            session.setCfg(cfg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                config.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据传入的参数，解析XML，并封装到Map中
     *
     * @param mapperPath 映射配置文件位置
     * @return map中包含了获取的唯一标识（key是由dao的全限定类名和方法名组成）
     * 以及执行所必需的必要信息（value是一个Mapper对象，里面存放的是执行的SQL语句
     * 和要封装的结果类型的全限定类名）
     */
    private static Map<String, Mapper> loadMapperConfiguration(String mapperPath) {
        InputStream in = null;
        try {
            //定义返回对象
            Map<String, Mapper> mappers = new HashMap<String, Mapper>();
            //1.根据路径获取字节输入流
            in = Resources.getResourceAsStream(mapperPath);
            //2.根据字节输入流获取Document对象
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            //3.获取根节点
            Element root = document.getRootElement();
            //4.根据根节点的namespace属性取值
            String namespace = root.attributeValue("namespace");//这是组成map中key的部分
            //5.获取所有的select节点
            List<Element> selectElements = root.selectNodes("//select");
            //6.遍历select节点集合
            for (Element selectElement : selectElements) {
                //取出id属性的值 组成map中key的一部分
                String id = selectElement.attributeValue("id");
                //取出resultType属性的值 组成map中value的部分
                String resultType = selectElement.attributeValue("resultType");
                //取出文本内容 组成map中value的部分
                String queryString = selectElement.getText();
                //创建key
                String key = namespace + "." + id;
                //创建value
                Mapper mapper = new Mapper();
                mapper.setQueryString(queryString);
                mapper.setResultType(resultType);
                //把key和value存入mappers中
                mappers.put(key, mapper);
            }

            return mappers;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据传入的参数，得到 dao 中所有被 select 注解标注的方法。
     * 根据方法名称和类名，以及方法上注解 value 属性的值，组成 Mapper 的必要信息
     * @param daoClassPath
     * @return
     */
   /* private static Map<String,Mapper> loadMapperAnnotation(String daoClassPath) {
        //定义返回值对象
        Map<String,Mapper> mappers = new HashMap<String, Mapper>();

        try {
            //1.得到dao接口的字节码对象
            Class daoClass = Class.forName(daoClassPath);
            //2.得到dao接口的方法数组
            Method[] methods = daoClass.getMethods();
            //3.遍历Method数组
            for(Method method : methods) {
                //取出每一个方法，判断是否有select注解
                boolean isAnnotated = method.isAnnotationPresent(Select.class);
                if(isAnnotated) {
                    //创建Mapper对象
                    Mapper mapper = new Mapper();
                    //取出注解的value属性值
                    Select selectAnno = method.getAnnotation(Select.class);
                    String queryString = selectAnno.value();
                    mapper.setQueryString(queryString);
                    //获取当前方法的返回值类型，还要求必须带有泛型信息
                    Type type = method.getGenericReturnType();//List<User>
                    //判断Type是不是参数化的类型
                    if(type instanceof ParameterizedType) {
                        //强转
                        ParameterizedType ptype = (ParameterizedType) type;
                        //得到参数化类型中的实际类型参数
                        Type[] types = ptype.getActualTypeArguments();
                        //取出第一个
                        Class domainClass = (Class)types[0];
                        //获取domainClass的类名
                        String resultType = domainClass.getName();
                        //给Mapper赋值
                        mapper.setResultType(resultType);
                    }
                    //组装keyd的信息
                    //获取方法的名称
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();
                    String key = className+"."+methodName;
                    //给map赋值
                    mappers.put(key,mapper);
                }
            }

            return mappers;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
