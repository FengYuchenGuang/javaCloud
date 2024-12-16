package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;
import org.springframework.stereotype.Service;

/**
 * @author hxz
 */
public interface IUserService extends IService<User> {

    void deductBalance(Long id, Integer money);
}
