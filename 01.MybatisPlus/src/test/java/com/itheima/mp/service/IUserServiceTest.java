package com.itheima.mp.service;

import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author hxz
 */
@SpringBootTest
class IUserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    void TestSaveUser(){
        User user = new User();
        user.setUsername("zxy");
        user.setPassword("1314520");
        user.setPhone("1868687247");
        user.setBalance(1500);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);
    }

    @Test
    void testQueryByIds() {

        List<User> users = userService.listByIds(List.of(6L, 7L, 8L, 9L));
        users.forEach(System.out::println);
    }

    /**
     * 一个一个新增
     * 总共 耗时：51091 ms
     */
    @Test
    void testSaveOneByOne() {
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            userService.save(buildUser(i));
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

    private User buildUser(int i) {
        User user = new User();
        user.setUsername("user_" + i);
        user.setPassword("123");
        user.setPhone("" + (18688190000L + i));
        user.setBalance(2000);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(user.getCreateTime());
        return user;
    }

    /**
     * 批量新增
     *
     * 1、使用批量新增语句，效果提升是 一个一个的 10 倍
     *
     * 加入
     * 2、MySQL的客户端连接参数中有这样的一个参数：rewriteBatchedStatements。顾名思义，就是重写批处理的statement语句。
     *   这个参数的默认值是 false，我们需要修改连接参数，将其配置为 true
     *   修改项目中的 application.yml文件，在 jdbc 的 url 后面添加参数 &rewriteBatchedStatements=true
     *
     *
     *  与最初相比一共提升 15倍
     * 总共 耗时：3308 ms
     */
    @Test
    void testSaveBatch() {
        // 准备10万条数据
        List<User> list = new ArrayList<>(1000);
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            list.add(buildUser(i));
            // 每1000条批量插入一次
            if (i % 1000 == 0) {
                userService.saveBatch(list);
                list.clear();
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

}