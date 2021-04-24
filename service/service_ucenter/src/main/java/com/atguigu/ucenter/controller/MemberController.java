package com.atguigu.ucenter.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.ucenter.entity.Member;
import com.atguigu.ucenter.entity.vo.RegisterVo;
import com.atguigu.ucenter.service.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-04-14
 */
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin
public class MemberController {

    @Autowired
    private MemberService memberService;

    //登录
    @PostMapping("login")
    public R loginUser(@RequestBody Member member){
        //调用service方法实现登录
        //返回token值，只用jwt生成
        String token = memberService.login(member);

        return R.ok().data("token",token);
    }

    //注册
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);
        return R.ok();
    }

    //根据token获取用户信息
    @GetMapping("getMemberInfo")
    public R getMemberInfo(HttpServletRequest request){
        //调用jwt工具类的方法，根据request对象获取头信息，返回用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //查询数据库，根据用户id获取用户信息
        Member member = memberService.getById(memberId);
        return R.ok().data("userInfo",member);
    }

    //实现用户id获取用户信息，返回用户信息对象
    @PostMapping("getMemberInfoById/{id}")
    public UcenterMemberVo getMemberInfoById(@PathVariable String id){
        Member memberInfo = memberService.getById(id);
        UcenterMemberVo ucenterMemberVo = new UcenterMemberVo();
        BeanUtils.copyProperties(memberInfo,ucenterMemberVo);
        return ucenterMemberVo;
    }

    //查询某一天的注册人数
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = memberService.countRegisterDay(day);
        return R.ok().data("countRegister",count);
    }


}

