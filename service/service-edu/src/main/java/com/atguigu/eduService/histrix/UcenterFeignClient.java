package com.atguigu.eduService.histrix;

import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduService.client.UcenterClient;
import org.springframework.stereotype.Component;

@Component
public class UcenterFeignClient implements UcenterClient {

    @Override
    public UcenterMemberVo getMemberInfoById(String id) {
        return null;
    }
}
