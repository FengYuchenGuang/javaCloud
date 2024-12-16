package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxz
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     *
     * 与 lambdaQuery 方法类似，IService 中的 lambdaUpdate 方法可以非常方便的实现复杂更新业务。
     * 例如下面的需求：
     * 需求：改造根据id修改用户余额的接口，要求如下
     *      - 如果扣减后余额为0，则将用户status修改为冻结状态（2）
     */
    @Override
    public void deductBalance(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == 2) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额
//        baseMapper.deductMoneyById(id, money);
        /**
         * 只有加上 update() 语句才会执行。 否则只是生成语句。
         */
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, 2)
                .eq(User::getId, id)
                .update();
    }

    /**
     * 可以发现lambdaQuery方法中除了可以构建条件，还需要在链式编程的最后添加一个list()，
     * 这是在告诉MP我们的调用结果需要是一个list集合。这里不仅可以用list()，可选的方法有：
     * - .one()：最多1个结果
     * - .list()：返回集合结果
     * - .count()：返回计数结果
     * MybatisPlus会根据链式编程的最后一个方法来判断最终的返回结果。
     */
    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        List<User> users = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();

        return users;
    }

    @Override
    public UserVO queryUserAndAddressById(Long userId) {
        // 1.查询用户
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        // 2.查询收货地址
        List<Address> addresses = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, userId)
                .list();
        // 3.处理vo
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
        return userVO;
    }

}
