package com.atguigu.eduorder.histrix;

import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduorder.client.UcenterClient;
import org.springframework.stereotype.Component;

@Component
public class UcenterFeignClient implements UcenterClient {

    @Override
    public UcenterMemberVo getMemberInfoById(String id) {
        return null;
    }

}
