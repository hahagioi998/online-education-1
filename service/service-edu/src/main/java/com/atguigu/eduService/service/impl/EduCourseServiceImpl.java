package com.atguigu.eduService.service.impl;

import com.atguigu.eduService.entity.EduCourse;
import com.atguigu.eduService.entity.EduCourseDescription;
import com.atguigu.eduService.entity.EduTeacher;
import com.atguigu.eduService.entity.frontvo.CourseFrontVo;
import com.atguigu.eduService.entity.frontvo.CourseWebVo;
import com.atguigu.eduService.entity.vo.CourseInfoVo;
import com.atguigu.eduService.entity.vo.CoursePublishVo;
import com.atguigu.eduService.mapper.EduCourseMapper;
import com.atguigu.eduService.service.*;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-04-07
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService descriptionService;

    @Autowired
    private EduVideoService videoService;   //小节

    @Autowired
    private EduChapterService chapterService;   //章节


    //添加课程信息
    @Override
    public String saveCourseInfo(CourseInfoVo couseInfoVo) {
        //1.向课程表添加课程基本信息,将CouseInfoVo转换为EduCourse
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(couseInfoVo,eduCourse);
        int count = baseMapper.insert(eduCourse);
        if (count == 0){
            throw new GuliException(20001,"添加课程信息失败");
        }

        String cid = eduCourse.getId();
        //2.向课程简介表添加课程简介edu_course_description
        EduCourseDescription description = new EduCourseDescription();
        description.setId(cid);
        description.setDescription(couseInfoVo.getDescription());
        descriptionService.save(description);

        return cid;
    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //1.查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse,courseInfoVo);

        //2.查询课程简介表
        EduCourseDescription courseDescription = descriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());

        return courseInfoVo;
    }

    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //1.修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int count = baseMapper.updateById(eduCourse);
        if (count != 1){
            throw new GuliException(20001,"修改课程信息失败");
        }

        //2.修改课程信息表
        EduCourseDescription courseDescription = new EduCourseDescription();
        BeanUtils.copyProperties(courseInfoVo,courseDescription);
        descriptionService.updateById(courseDescription);
    }

    @Override
    public CoursePublishVo getPublishCourseInfo(String courseId) {
        CoursePublishVo courseInfo = baseMapper.getPublishCourseInfo(courseId);
        return courseInfo;
    }

    @Override
    public void removeCourse(String courseId) {
        try{
            //1.根据课程id删除小节(一对多)
            videoService.removeVideoByCourseId(courseId);

            //2.根据课程id删除章节(一对多)
            chapterService.removeChapterByCourseId(courseId);

            //3.根据课程id删除课程简介(一对一)
            descriptionService.removeById(courseId);

            //4.删除课程
            baseMapper.deleteById(courseId);
        }catch (Exception e){
            throw new GuliException(20001,"删除课程失败");
        }

    }

    //查询前8个热门课程
    @Cacheable(key = "'selectCourseList'",value = "course")
    @Override
    public List<EduCourse> queryCourse() {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        wrapper.last("limit 8");
        List<EduCourse> courseList = baseMapper.selectList(wrapper);
        return courseList;
    }

    //1.条件查询带分页查询课程
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> coursePage, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //判断条件值是否为空，不为空拼接
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())){   //一级分类是否为空
            wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());
        }
        if (!StringUtils.isEmpty(courseFrontVo.getSubjectId())){   //二级分类是否为空
            wrapper.eq("subject_id",courseFrontVo.getSubjectId());
        }
        if (!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())){   //关注度排序
            wrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())){   //最新排序
            wrapper.orderByDesc("gmt_create");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())){   //价格排序
            wrapper.orderByDesc("price");
        }

        //把分页数据封装到CoursePage对象
        baseMapper.selectPage(coursePage,wrapper);

        List<EduCourse> records = coursePage.getRecords();
        long current = coursePage.getCurrent();
        long pages = coursePage.getPages();
        long size = coursePage.getSize();
        long total = coursePage.getTotal();

        boolean hasNext = coursePage.hasNext();
        boolean hasPrevious = coursePage.hasPrevious();

        //把分页数据获取出来，放到map集合
        Map<String,Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    //根据课程id，编写SQL语句查询课程信息
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }


}
