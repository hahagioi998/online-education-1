package com.atguigu.eduService.service;

import com.atguigu.eduService.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author testjava
 * @since 2021-04-03
 */
public interface EduTeacherService extends IService<EduTeacher> {

    List<EduTeacher> queryTeacher();

    Map<String, Object> getTeacherFrontList(Page<EduTeacher> teacherPage);
}
