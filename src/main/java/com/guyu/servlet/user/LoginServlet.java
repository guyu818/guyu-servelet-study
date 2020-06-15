package com.guyu.servlet.user;

import com.guyu.pojo.User;
import com.guyu.service.user.UserService;
import com.guyu.service.user.UserServiceImpl;
import com.guyu.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    //servlet控制层，调用业务层代码,这里new一个就好了

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet--start.....");
        //获取前端的用户名和密码
        String userCode=req.getParameter("userCode");
        String userPassword=req.getParameter("userPassword");
        System.out.println(userCode+userPassword);

        //和数据库中的密码做对比，调用业务层
        UserService userService=new UserServiceImpl();
        User user = userService.login(userCode, userPassword);
        //比较代码这里有一个坑user.getUserPassword()==userPassword不管用，需要用equels来比较
        //而且需要先判断user是否为空，确保user不为空的情况下再去获取密码数据
        if((user!=null) && userPassword.equals(user.getUserPassword())){//这个人没问题可以登录
            //将用户的信息存到session中
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {//账户密码信息有误
            //提示错误的信息
            req.setAttribute("error","用户名和密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
