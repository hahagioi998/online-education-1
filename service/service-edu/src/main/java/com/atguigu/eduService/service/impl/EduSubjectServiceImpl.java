package com.atguigu.eduService.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduService.entity.EduSubject;
import com.atguigu.eduService.entity.excel.SubjectData;
import com.atguigu.eduService.entity.subject.OneSubject;
import com.atguigu.eduService.entity.subject.TwoSubject;
import com.atguigu.eduService.listener.SubjectExcelListener;
import com.atguigu.eduService.mapper.EduSubjectMapper;
import com.atguigu.eduService.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-04-06
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            InputStream in = file.getInputStream();
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //1.查询所有一级分类    parent_id = 0
        QueryWrapper<EduSubject> oneSubjectWapper = new QueryWrapper<>();
        oneSubjectWapper.eq("parent_id","0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(oneSubjectWapper);


        //2.查询所有二级分类    parent_id != 0
        QueryWrapper<EduSubject> twoSubjectWapper = new QueryWrapper<>();
        twoSubjectWapper.ne("parent_id","0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(twoSubjectWapper);

        //创建List集合，存储最终封装数据
        List<OneSubject> finalSubjectList1 = new ArrayList<>();

        //3.封装一级分类
        //查询出来所有的一级分类list集合遍历，得到每一个一级分类对象，获取每个一级分类对象值，封装到finalSubjectList集合
        for (int i = 0; i <oneSubjectList.size() ; i++) {
            EduSubject eduSubject1 = oneSubjectList.get(i);
            OneSubject oneSubject = new OneSubject();
            //oneSubject.setId(eduSubject.getId());
            //oneSubject.setTitle(eduSubject.getTitle());
            BeanUtils.copyProperties(eduSubject1,oneSubject);    //将eduSubject的值拷贝到oneSubject
            finalSubjectList1.add(oneSubject);

            //4.分装二级分类
            List<TwoSubject> finalSubjectList2 = new ArrayList<>();
            for (int j = 0; j < twoSubjectList.size(); j++) {
                EduSubject eduSubject2 = twoSubjectList.get(j);
                //判断二级分类的parent_id和一级分类的id是否一样，一样则证明此二级分类属于一级分类
                if (eduSubject2.getParentId().equals(eduSubject1.getId())){
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(eduSubject2,twoSubject);
                    finalSubjectList2.add(twoSubject);
                }
            }
            //把一级分类下面所有的二级分类放到一级分类里面
            oneSubject.setChildren(finalSubjectList2);
        }

        return finalSubjectList1;
    }
}
