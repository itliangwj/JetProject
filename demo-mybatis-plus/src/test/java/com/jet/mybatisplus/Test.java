package com.jet.mybatisplus;

import com.jet.mybatisplus.entity.User;
import com.jet.mybatisplus.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2021/12/5 18:02
 */
@SpringBootTest
public class Test {
    @Resource
    private UserMapper userMapper;

    @org.junit.jupiter.api.Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assertions.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }
}
