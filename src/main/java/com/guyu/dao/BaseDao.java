package com.guyu.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Des 操作数据库的基类
 * @Author guyu
 * @Date 2020/4/22 8:57
 * @Param
 * @Return
 */
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //静态代码块，类加载的时候就初始化了
    /*
    拿到资源文件的信息
     */
    static {
        Properties properties = new Properties();
        //通过类加载器读取对应的资源文件
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver=properties.getProperty("driver");
        url=properties.getProperty("url");
        username=properties.getProperty("username");
        password=properties.getProperty("password");
    }
    //获取connection连接
    public static Connection getConnection() {
        Connection connection =null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    //编写查找的工具方法
    public static ResultSet execute(Connection connection,String sql,Object[] params,ResultSet resultSet,PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        //setObject,占位符是从1开始的，但是我们的数组是从0开始的
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
//        ResultSet resultSet = preparedStatement.executeQuery(sql);
        //预编译的sql直接执行就可以了，不需要传参，传了参也不会报错
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    //编写增删该的工具方法
    public static int execute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        //setObject,占位符是从1开始的，但是我们的数组是从0开始的
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
//        ResultSet resultSet = preparedStatement.executeQuery(sql);
        //预编译的sql直接执行就可以了，不需要传参，传了参也不会报错
        int  updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //关闭连接
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag=true;

        if (resultSet!=null) {
            try {
                resultSet.close();
                //GC回收,确保回收
                resultSet=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;//没有释放成功
            }
        }
        if (preparedStatement!=null) {
            try {
                preparedStatement.close();
                //GC回收,确保回收
                preparedStatement=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;//没有释放成功
            }
        }
        if (connection!=null) {
            try {
                connection.close();
                //GC回收,确保回收
                connection=null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag=false;//没有释放成功
            }
        }
        return flag;//都释放成功返回true
    }

}
