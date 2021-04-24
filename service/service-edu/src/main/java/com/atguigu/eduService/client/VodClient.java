package com.atguigu.eduService.client;

import com.atguigu.commonutils.R;
import com.atguigu.eduService.histrix.MyFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "service-vod",fallback = VodFileDegradeFeignClient.class)
@FeignClient(name = "service-vod",fallbackFactory = MyFallbackFactory.class)
@Component
public interface VodClient {

    //定义要调用的方法路径
    //根据视频id删除阿里云中的视频(PathVariable一定要指定名称，否则报错)
    @DeleteMapping("/eduvod/video/removeAlyVideo/{id}")
    R removeAlyVideo(@PathVariable("id") String id);

    //删除多个阿里云视频的方法
    @DeleteMapping("/eduvod/video/deleteBatch")
    R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList);
}
