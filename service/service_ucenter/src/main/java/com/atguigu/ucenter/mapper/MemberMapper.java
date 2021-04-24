package com.atguigu.ucenter.mapper;

import com.atguigu.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2021-04-14
 */
public interface MemberMapper extends BaseMapper<Member> {

    int countRegisterDay(String day);
}
