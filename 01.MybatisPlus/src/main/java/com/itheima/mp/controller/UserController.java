package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hxz
 */
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    /**
     * 可以使用 @Autowired 方式 注入，但是 SpringBoot 不推荐
     *
     */
//    @Autowired
//    private IUserService userService;

    /**
     * 使用 lombok 的 @RequiredArgsConstructor
     * 构建必备的 构造器 -> 即对哪些一开始需要初始化 的函数 进行构造
     * 通过 在 private IUserService userService; 中
     * 添加 final
     * 使 IUserService 类 在 构造器初始化过程 中 被注入
     *
     * 对于哪些不需要 初始化注入的函数 不加 final 即可
     */
    private final IUserService userService;

    /**
     * @RequestBody  才能读取JSON格式
     * @param userFormDTO
     */
    @PostMapping
    @ApiOperation("新增用户")
    public void saveUser(@RequestBody UserFormDTO userFormDTO){
        // 1.转换DTO为PO
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    /**
     * @PathVariable("id") 路径占位符
     *
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public void removeUserById(@ApiParam("用户id") @PathVariable("id") Long userId){
        userService.removeById(userId);
    }

    /**
     *
     * 旧代码，只查询 用户信息
     */
    @GetMapping("/query1_{id}")
    @ApiOperation("根据id查询用户")
    public UserVO queryUserById_1(@ApiParam("用户id") @PathVariable("id") Long userId){
        // 1.查询用户
        User user = userService.getById(userId);
        // 2.处理vo
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户 和 相关地址信息")
    public UserVO queryUserById(@ApiParam("用户id") @PathVariable("id") Long userId){

        return userService.queryUserAndAddressById(userId);
    }

    @GetMapping
    @ApiOperation("根据id集合查询用户")
    public List<UserVO> queryUserByIds(@ApiParam("用户id集合") @RequestParam("ids") List<Long> ids){
        // 1.查询用户
        List<User> users = userService.listByIds(ids);
        // 2.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @PutMapping("{id}/deduction/{money}")
    @ApiOperation("扣减用户余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money")Integer money){
        userService.deductBalance(id, money);
    }

    /**
     * 实现一个根据复杂条件查询用户的接口，查询条件如下：
     * - name：用户名关键字，可以为空
     * - status：用户状态，可以为空
     * - minBalance：最小余额，可以为空
     * - maxBalance：最大余额，可以为空
     * 可以理解成一个用户的后台管理界面，管理员可以自己选择条件来筛选用户，因此上述条件不一定存在，需要做判断。
     */
    @GetMapping("/list1")
    @ApiOperation("方法1：根据id集合查询用户")
    public List<UserVO> queryUsers_1(UserQuery query){
        // 1.组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance);
        // 2.查询用户
        List<User> users = userService.list(wrapper);
        // 3.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

    /**
     * 上述条件构建的代码太麻烦了。
     * 因此 Service 中对 LambdaQueryWrapper 和 LambdaUpdateWrapper 的用法进一步做了简化。
     * 我们无需自己通过 new 的方式来创建 Wrapper，而是直接调用 lambdaQuery 和 lambdaUpdate 方法
     *
     * 可以发现lambdaQuery方法中除了可以构建条件，还需要在链式编程的最后添加一个list()，
     * 这是在告诉MP我们的调用结果需要是一个list集合。这里不仅可以用list()，可选的方法有：
     * - .one()：最多1个结果
     * - .list()：返回集合结果
     * - .count()：返回计数结果
     * MybatisPlus会根据链式编程的最后一个方法来判断最终的返回结果。
     */
    @GetMapping("/list2")
    @ApiOperation("方法2：根据id集合查询用户")
    public List<UserVO> queryUsers_2(UserQuery query){
        // 1.组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();
        // 2.查询用户
        List<User> users = userService.lambdaQuery()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
        // 3.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @GetMapping("/list")
    @ApiOperation("方法3：根据id集合查询用户，简化代码")
    public List<UserVO> queryUsers(UserQuery query){
        // 1.查询用户 PO
        List<User> users = userService.queryUsers(query.getName(), query.getStatus(), query.getMinBalance(), query.getMaxBalance());

        // 2.把 PO 拷贝到 VO
        return BeanUtil.copyToList(users, UserVO.class);
    }


}
