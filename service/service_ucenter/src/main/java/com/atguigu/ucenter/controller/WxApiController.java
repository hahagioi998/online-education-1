package com.atguigu.ucenter.controller;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.ucenter.entity.Member;
import com.atguigu.ucenter.service.MemberService;
import com.atguigu.ucenter.utils.ConstantWxUtils;
import com.atguigu.ucenter.utils.HttpClientUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@Controller
//@CrossOrigin
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private MemberService memberService;

    //2.获取扫描人信息，添加数据
    @GetMapping("callback")
    public String callback(String code,String state){
        //1.获取code值，临时票据，类似于验证码

        //2.拿着code请求微信固定的地址，得到两个值 accsess_token 和 openid
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        //拼接3个参数，id，密钥和code值
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_APP_SECRET,
                code);

        //请求这个拼接好的地址，得到返回的两个值
        //使用HttpClient发送请求，得到返回结果
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //从accessTokenInfo字符串获取出来两个值，access_token和openid
            //把accessTokenInfo字符串转换成map集合，根据map里面key获取对应值
            //使用json转换工具 Gson
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");

            //把扫描人信息添加到数据库里面，自动注册
            //判断数据库表里面是否存在相同微信信息,根据openid判断
            Member member = memberService.getOpenIdMember(openid);
            if (member == null){    //member是空，表没有相同微信数据，进行添加
                //3.拿着access_token和open_id,再去请求微信提供固定的地址，获取到扫描人信息
                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接两个参数
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);

                //发送请求
                String userInfo = HttpClientUtils.get(userInfoUrl);

                //获取返回userInfo字符串扫描人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname");     //昵称
                String headimgurl = (String) userInfoMap.get("headimgurl"); //头像

                member = new Member();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }

            //cookie无法跨域，使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());

            //最后：返回首页面,通过路径传递token字符串
            return "redirect:http://localhost:3000?token=" + jwtToken;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"登录失败");
        }
    }

    //1.生成微信扫描的二维码
    @GetMapping("login")
    public String getWxCode(){
        //固定地址，后面拼接参数
        // 微信开放平台授权baseUrl,%s相当于?标识占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //对redirect_url进行URLEncoding编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //设置%s里面的值
        String url = String.format(baseUrl, ConstantWxUtils.WX_OPEN_APP_ID, redirectUrl, "atguigu");

        //请求微信地址
        return "redirect:"+url;
    }
}
