package org.example.controllers;
import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")
public class OrderItemController {
    @Autowired
    private OrderItemDao orderItemDao;
    @GetMapping
    public List<OrderItem> getAll(@RequestParam(required = false) Integer orderId) {
        if (orderId != null) {
            return orderItemDao.getOrderItemsByOrderId(orderId);
        }
        return orderItemDao.getOrderItems();
    }
    @GetMapping("/{id}")
    public OrderItem get(@PathVariable int id) {
        OrderItem item = orderItemDao.getOrderItemById(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
        return item;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem create(@RequestBody OrderItem item) {
        return orderItemDao.createOrderItem(item);
    }
    @PutMapping("/{id}")
    public OrderItem update(@PathVariable int id, @RequestBody OrderItem item) {
        item.setId(id);
        try {
            return orderItemDao.updateOrderItem(item);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
    }
    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        int rows = orderItemDao.deleteOrderItem(id);
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
        return rows;
    }
}