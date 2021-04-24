package com.atguigu.eduService.histrix;

import com.atguigu.commonutils.R;
import com.atguigu.eduService.client.VodClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyFallbackFactory implements FallbackFactory<VodClient> {

    @Override
    public VodClient create(Throwable cause) {
        return new VodClient() {
            @Override
            public R removeAlyVideo(String id) {
                System.out.println("降级方法触发=========================================");
                return R.error().message("服务熔断了");
            }

            @Override
            public R deleteBatch(List<String> videoIdList) {
                return R.error().message("服务熔断了");
            }
        };
    }
}
