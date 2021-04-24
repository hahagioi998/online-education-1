package com.atguigu.eduorder.client;

import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduorder.histrix.UcenterFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-ucenter",fallback = UcenterFeignClient.class)
@Component
public interface UcenterClient {
    //实现用户id获取用户信息，返回用户信息对象
    @PostMapping("/educenter/member/getMemberInfoById/{id}")
    public UcenterMemberVo getMemberInfoById(@PathVariable("id") String id);


}
