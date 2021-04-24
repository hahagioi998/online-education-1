package com.atguigu.eduorder.histrix;

import com.atguigu.commonutils.vo.CourseInfoVo;
import com.atguigu.eduorder.client.EduCourseClient;
import org.springframework.stereotype.Component;

@Component
public class EduCourseFeignClient implements EduCourseClient {
    @Override
    public CourseInfoVo getCourseInfoOrder(String id) {
        return null;
    }
}
