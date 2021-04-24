package com.atguigu.ucenter.service.impl;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5Util;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.ucenter.entity.Member;
import com.atguigu.ucenter.entity.vo.RegisterVo;
import com.atguigu.ucenter.mapper.MemberMapper;
import com.atguigu.ucenter.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-04-14
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public String login(Member member) {
        String mobile = member.getMobile();
        String password = member.getPassword();
        //手机号和密码非空判断
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            throw  new GuliException(20001,"登录失败");
        }

        //判断手机号是否正确
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Member mobileMember = baseMapper.selectOne(wrapper);

        //判断查询对象是否为空
        if (mobileMember == null){  //数据库中没有此手机号
            throw new GuliException(20001,"手机号错误");
        }

        //判断密码
        if (!MD5Util.getMD5(password).equals(mobileMember.getPassword())){  //如果传过来的密码和数据库的密码不一致
            throw new GuliException(20001,"密码错误");
        }
        System.out.println(MD5Util.getMD5(password));

        //判断用户是否被禁用
        if (mobileMember.getIsDisabled()){
            throw new GuliException(20001,"账号被禁用");
        }

        //登录成功,使用jwt工具类生成token
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());

        return jwtToken;
    }

    @Override
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String mobile = registerVo.getMobile(); //手机号
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码

        //非空判断
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(nickname) || StringUtils.isEmpty(password)){
            throw new GuliException(20001,"信息未全部填写，注册失败");
        }

        //判断验证码
        String rediscode = redisTemplate.opsForValue().get(mobile);
        if (!code.equals(rediscode)){
            throw new GuliException(20001,"验证码错误");
        }

        //判断数据库中手机号是否重复
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0){
            throw new GuliException(20001,"手机号重复");
        }

        //添加数据到数据库中
        Member member = new Member();
        member.setMobile(mobile);
        member.setPassword(MD5Util.getMD5(password));   //密码需要做MD5加密
        member.setNickname(nickname);
        member.setIsDisabled(false);
        member.setAvatar("shanghai.aliyuncs.com/2021/04/06/235befde39e54d5a8b80c2631f7a3ad8file.png");
        baseMapper.insert(member);

    }

    //根据openid查询
    @Override
    public Member getOpenIdMember(String openid) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid);
        Member member = baseMapper.selectOne(wrapper);
        return member;
    }

    @Override
    public int countRegisterDay(String day) {
        int count = baseMapper.countRegisterDay(day);
        if (count == 0){
            throw new GuliException(20001,"当天没有注册人数");
        }
        return count;
    }
}
