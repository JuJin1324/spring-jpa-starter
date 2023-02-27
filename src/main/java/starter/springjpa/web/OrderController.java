package starter.springjpa.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import starter.springjpa.domain.order.dto.OrderReadDto;
import starter.springjpa.domain.order.service.OrderService;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/02/27
 */

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("")
    public Page<OrderReadDto> getOrderPage(@PageableDefault(page = 0, size = 20, sort = "orderDateUTC,desc") Pageable pageable) {
        return null;
    }
}
