package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
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

    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public UserVO queryUserById(@ApiParam("用户id") @PathVariable("id") Long userId){
        // 1.查询用户
        User user = userService.getById(userId);
        // 2.处理vo
        return BeanUtil.copyProperties(user, UserVO.class);
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
}
