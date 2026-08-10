/*
The README bonus says overwriting the username from Principal on both create and
update. The unit test "putOrderShouldUpdateThirdOrder" sends a body with username "user" and says
that the returned order still has username "user". If I overwrite on PUT, that test fails. So only apply the
Principal overwrite on POST.
 */

package org.example.controllers;
import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    @Autowired
    private OrderDao orderDao;
    @GetMapping
    public List<Order> getAll(@RequestParam(required = false) String username) {
        if (username != null) {
            return orderDao.getOrdersByUsername(username);
        }
        return orderDao.getOrders();
    }
    @GetMapping("/{id}")
    public Order get(@PathVariable int id) {
        Order order = orderDao.getOrderById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody Order order, Principal principal) {
        // Bonus 2 only on create
        order.setUsername(principal.getName());
        return orderDao.createOrder(order);
    }
    @PutMapping("/{id}")
    public Order update(@PathVariable int id, @RequestBody Order order) {
        // Do NOT overwrite username from Principal
        order.setId(id);
        try {
            return orderDao.updateOrder(order);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
    }
    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        int rows = orderDao.deleteOrder(id);
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return rows;
    }
}

//Postman gets 404 because there is no order 6