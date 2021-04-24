package com.atguigu.eduService.histrix;

import com.atguigu.commonutils.R;
import com.atguigu.eduService.client.VodClient;
import org.springframework.stereotype.Component;

import java.util.List;

//服务熔断降级处理类
@Component
public class VodFileDegradeFeignClient implements VodClient {
    @Override
    public R removeAlyVideo(String id) {
        return R.error().message("删除视频出错了");
    }

    @Override
    public R deleteBatch(List<String> videoIdList) {
        return R.error().message("删除多个视频出错了");
    }
}
