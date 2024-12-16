package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * ======================================
     *
     *  使用静态工具查询
     *
     * ======================================
     */
    @Test
    void testDbGet() {
        User user = Db.getById(1L, User.class);
        System.out.println(user);
    }

    @Test
    void testDbList() {
        // 利用Db实现复杂条件查询
        List<User> list = Db.lambdaQuery(User.class)
                .like(User::getUsername, "o")
                .ge(User::getBalance, 1000)
                .list();
        list.forEach(System.out::println);
    }

    @Test
    void testDbUpdate() {
        Db.lambdaUpdate(User.class)
                .set(User::getBalance, 2000)
                .eq(User::getUsername, "Rose")
                .update();
    }


    /**
     * 以下演示使用 mybatis 创建函数 与 使用 mybatisplus
     */
    @Test
    void testInsert() {
        User user = new User();
//        user.setId(5L);
        user.setUsername("hajah");
        user.setPassword("1314520");
        user.setPhone("1868687247");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
//        userMapper.saveUser(user);
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
//        User user = userMapper.queryUserById(5L);
        User user = userMapper.selectById(6);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
//        List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L));
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(6575855);
//        userMapper.updateUser(user);
        userMapper.updateById(user);
    }

    @Test
    void testDeleteUser() {
//        userMapper.deleteUser(5L);
        userMapper.deleteById(5L);
    }

    @Test
    void test1(){
        //构建查询条件
        /**
         * select("id","username","info","balance")
         * 这样写死 字段名 其实不好
         */
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .select("id","username","info","balance")
                .like("username","o")
                .ge("balance",1000);
        //查询
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);

    }

    /**
     * 字段名不写死
     */
    @Test
    void testLambda(){
        //构建查询条件
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .select(User::getId,User::getUsername,User::getInfo,User::getBalance)
                .like(User::getUsername,"o")
                .ge(User::getBalance,1000);
        //查询
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        users.forEach(System.out::println);

    }

    @Test
    void test2(){
        //1、要更新的数据
        User user = new User();
        user.setBalance(2000);
        //2、更新条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("username","jack");
        //3、执行更新
        userMapper.update(user,wrapper);

    }

    @Test
    void test3(){
        /**
         * 更新 id 为 1，2，4 的用户，balance - 200
         */

        List<Integer> ids = List.of(1, 2, 3);
        //1、更新条件
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance - 200")
                .in("id",ids);
        //2、执行更新
        userMapper.update(null,wrapper);

    }

    /**
     * 自定义sql
     *  balance = balance + 200
     */
    @Test
    void testCustom(){
        // 1、更新条件
        List<Integer> ids = List.of(1, 2, 4);
        int amount = 200;
        // 2、定义条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("id", ids);
        // 3、调用自定义sql
        userMapper.updateBalanceByIds(wrapper, amount);
    }


}