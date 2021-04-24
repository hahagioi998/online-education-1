package com.atguigu.eduorder.client;

import com.atguigu.commonutils.vo.CourseInfoVo;
import com.atguigu.eduorder.histrix.EduCourseFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-edu",fallback = EduCourseFeignClient.class)
@Component
public interface EduCourseClient {
    //.根据课程id查询课程信息
    @PostMapping("/eduservice/coursefront/getCourseInfoOrder/{id}")
    public CourseInfoVo getCourseInfoOrder(@PathVariable("id") String id);
}
