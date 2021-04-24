package com.atguigu.eduService.histrix;

import com.atguigu.eduService.client.OrderClient;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignClient implements OrderClient {
    @Override
    public boolean isBuyCourse(String courseId, String memberId) {
        return false;
    }
}
