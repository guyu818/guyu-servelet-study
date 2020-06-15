import org.junit.Test;

import java.sql.*;

/*
需要jar包的支持：

- java.sql
- javax.sql
- mysql-conneter-java…  连接驱动（必须要导入）

<!--mysql的驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
</dependency>

**JDBC 固定步骤：**

1. 加载驱动
2. 连接数据库,代表数据库
3. 向数据库发送SQL的对象Statement : CRUD
4. 编写SQL （根据业务，不同的SQL）
5. 执行SQL
6. 关闭连接
 */
public class JDBC {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //配置信息
        //解决乱码 useUnicode=true&characterEncoding=utf-8
        String url="jdbc:mysql://localhost:3306/smbms?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        //mysql驱动
        String driver="com.mysql.jdbc.Driver";
        String username="root";
        String password="guyu";

        //1.加载驱动
        Class.forName(driver);//需要抛出ClassNotFoundException，用反射寻找
        //2.连接数据库
        Connection connection = DriverManager.getConnection(url, username, password);//SQLException
        //3.向数据库发送SQL对象statement，preparedstatement:  CRUD 增删改查
        Statement statement = connection.createStatement();
        //4.编写sql语句
        String sql="select * from smbms_bill";
        //5.执行sql，返回一个ResultSet ：结果集
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            System.out.println("id="+rs.getObject("id"));
            System.out.println("productName="+rs.getObject("productName"));
        }

        //6.关闭连接，释放资源（一定要做，不要忘了，凡是有连接的不用了一定要释放）先开后关
        rs.close();
        statement.close();
        connection.close();
    }
    @Test
//    预编译SQL
    public void JDBC2() throws ClassNotFoundException, SQLException {
        String url="jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String driver="com.mysql.jdbc.Driver";
        String username="root";
        String password="guyu";

        //1.加载驱动
        Class.forName(driver);
        //2.连接数据库，获取连接
        Connection connection = DriverManager.getConnection(url, username, password);
        //3.编写sql
        String sql="INSERT INTO  stu(studentid,name,score) VALUES(?,?,?);";
        //4.预编译
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,155);//给第一个占位符？ 赋值为1111，因为它的数据类型为int
        preparedStatement.setString(2,"谷雨");//给第二个占位符？赋值
        preparedStatement.setInt(3,2222);//第三个

        //5.执行sql
        int i=preparedStatement.executeUpdate();//返回影响行数

        if (i>0) {
            System.out.println("插入成功");
        }

        String sql1="select * from stu";
        ResultSet resultSet = preparedStatement.executeQuery(sql1);

        while (resultSet.next()) {

            System.out.println("id="+resultSet.getObject("name"));
        }

        //关闭rs
        resultSet.close();
        //关闭连接
        preparedStatement.close();
        connection.close();

    }

}
