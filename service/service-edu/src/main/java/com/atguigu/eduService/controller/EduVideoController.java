package com.atguigu.eduService.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduService.client.VodClient;
import com.atguigu.eduService.entity.EduVideo;
import com.atguigu.eduService.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-04-07
 */
@RestController
@RequestMapping("/eduService/video")
//@CrossOrigin
public class EduVideoController {

    @Autowired
    private EduVideoService videoService;

    @Autowired
    private VodClient vodClient;  //注入调用服务的接口

    //添加小节
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        videoService.save(eduVideo);
        return R.ok();
    }

    //删除小节,删除对应的阿里云中的视频
    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id){
        //根据小节id获取视频id，调用方法实现视频删除
        EduVideo eduVideo = videoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();
        System.out.println("视频id=========" + videoSourceId);
        //判断小节里面是否有视频id，如果有删除视频
        if (!StringUtils.isEmpty(videoSourceId)){
            R result = vodClient.removeAlyVideo(videoSourceId);
            if (result.getCode() == 20001){
                throw new GuliException(20001,"删除视频失败，熔断器...");
            }
        }

        //删除小节
        videoService.removeById(id);
        return R.ok();
    }

    //修改小节 TODO
}

