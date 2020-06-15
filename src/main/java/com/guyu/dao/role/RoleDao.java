package com.guyu.dao.role;

import com.guyu.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RoleDao {
    //获取role列表
    public List<Role> getRoleList(Connection connection)throws Exception;
}
