package com.atguigu.eduService.service.impl;

import com.atguigu.eduService.entity.EduChapter;
import com.atguigu.eduService.entity.EduVideo;
import com.atguigu.eduService.entity.chapter.ChapterVo;
import com.atguigu.eduService.entity.chapter.VideoVo;
import com.atguigu.eduService.mapper.EduChapterMapper;
import com.atguigu.eduService.service.EduChapterService;
import com.atguigu.eduService.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-04-07
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService videoService;   //注入小节的service

    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {
        //1.根据课程id查询所有的章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id",courseId);
        List<EduChapter> eduChapterList = baseMapper.selectList(wrapperChapter);

        //2.根据课程id查询所有的小节
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        List<EduVideo> eduVideoList = videoService.list(wrapperVideo);

        //3.遍历查询章节list集合进行封装
        List<ChapterVo> chapterVoList = new ArrayList<>();
        for (EduChapter eduChapter : eduChapterList){
            //eduChapter的值拷贝到ChapterVo
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter,chapterVo);
            chapterVoList.add(chapterVo);

            //4.遍历查询小节list集合，进行封装
            List<VideoVo> videoVoList = new ArrayList<>();
            for (EduVideo eduVideo : eduVideoList){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo,videoVo);
                    videoVoList.add(videoVo);
            }
            //放封装之后的小节list集合，放到章节对象里面
            chapterVo.setChildren(videoVoList);
        }
        return chapterVoList;
    }

    @Override
    public boolean deleteChapter(String chapterId) {
        //根据章节id查询小节表，如果有数据则不删除
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",chapterId);
        int count = videoService.count(wrapper);    //如果有小节，count>0
        if (count > 0){
            //查询出小节，不进行删除
            throw new GuliException(20001,"有小节数据，无法删除");
        }else{
            //没有查询出小节，进行删除
            int result = baseMapper.deleteById(chapterId);
            return result>0;
        }
    }

    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
