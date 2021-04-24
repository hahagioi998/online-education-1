package com.atguigu.eduService.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduService.client.UcenterClient;
import com.atguigu.eduService.entity.EduComment;
import com.atguigu.eduService.service.EduCommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-04-20
 */
@RestController
@RequestMapping("/eduService/comment")
//@CrossOrigin
public class EduCommentController {

    @Autowired
    private UcenterClient ucenterClient;
    @Autowired
    private EduCommentService commentService;

    //1.根据课程id分页查询评论列表
    @GetMapping("commentPage/{courseId}/{page}/{limit}")
    public R commentPage(@PathVariable String courseId,@PathVariable long page,@PathVariable long limit){
        Page<EduComment> pageComment = new Page<>(page,limit);
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);   //根据课程id查询评论列表

        //把分页数据封装到pageComment对象
        commentService.page(pageComment,wrapper);

        List<EduComment> records = pageComment.getRecords();    //获取评论列表
        long current = pageComment.getCurrent();
        long pages = pageComment.getPages();
        long size = pageComment.getSize();
        long total = pageComment.getTotal();
        boolean hasNext = pageComment.hasNext();    //下一页
        boolean hasPrevious = pageComment.hasPrevious();      //上一页

        //把数据封装到map集合
        Map<String,Object> map = new HashMap<>();
        map.put("items",records);
        map.put("current",current);
        map.put("pages",pages);
        map.put("size",size);
        map.put("total",total);
        map.put("hasNext",hasNext);
        map.put("hasPrevious",hasPrevious);

        return R.ok().data(map);
    }

    //2.添加评论的接口(必须要用户登录，所以需要判断用户登录状态)
    @PostMapping("addComment")
    public R addComment(@RequestBody EduComment eduComment, HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);  //根据token获取用户id
        if (StringUtils.isEmpty(memberId)){
            return R.error().code(20001).message("请登录");
        }

        //远程服务调用，根据登录的用户id获取用户详细信息
        UcenterMemberVo memberInfo = ucenterClient.getMemberInfoById(memberId);

        eduComment.setMemberId(memberId);   //用户如果登录，将用户id值存入comment对象
        eduComment.setNickname(memberInfo.getNickname());
        eduComment.setAvatar(memberInfo.getAvatar());

        commentService.save(eduComment);
        return R.ok();
    }
}

