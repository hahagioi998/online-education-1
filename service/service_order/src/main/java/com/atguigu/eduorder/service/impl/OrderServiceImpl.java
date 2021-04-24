package com.atguigu.eduorder.service.impl;

import com.atguigu.commonutils.vo.CourseInfoVo;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduorder.client.EduCourseClient;
import com.atguigu.eduorder.client.UcenterClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.mapper.OrderMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.utils.UUIDUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-04-20
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private UcenterClient ucenterClient;    //注入用户模块对象
    @Autowired
    private EduCourseClient courseClient;

    @Override
    public String createOrder(String courseId, String memberId) {
        //通过远程调用根据用户id获取用户信息
        UcenterMemberVo memberInfo = ucenterClient.getMemberInfoById(memberId);

        //通过远程调用根据课程id获取课程信息
        CourseInfoVo courseInfo = courseClient.getCourseInfoOrder(courseId);

        //创建order对象，向order对象里面设置需要数据
        Order order = new Order();
        order.setOrderNo(UUIDUtil.getUUID()); //订单号uuid生成
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfo.getTitle());
        order.setCourseCover(courseInfo.getCover());
        order.setTeacherName(courseInfo.getTeacherName());
        order.setTotalFee(courseInfo.getPrice());
        order.setMemberId(memberId);
        order.setMobile(memberInfo.getMobile());
        order.setNickname(memberInfo.getNickname());

        order.setStatus(0);     //支付状态：0未支付，1已支付
        order.setPayType(1);    //1代表微信支付，2代表支付宝支付

        baseMapper.insert(order);

        return order.getOrderNo();    //返回订单号
    }
}
