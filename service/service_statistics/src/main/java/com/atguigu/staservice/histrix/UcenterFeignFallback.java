package com.atguigu.staservice.histrix;

import com.atguigu.commonutils.R;
import com.atguigu.staservice.client.UcenterClient;
import org.springframework.stereotype.Component;

@Component
public class UcenterFeignFallback implements UcenterClient {
    @Override
    public R countRegister(String day) {
        return R.error();
    }
}
