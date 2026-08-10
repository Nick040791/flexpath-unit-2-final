package org.example.daos;
import org.example.exceptions.DaoException;
import org.example.models.OrderItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
public class OrderItemDao {
    private final JdbcTemplate jdbcTemplate;
    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query(
                "SELECT * FROM order_items ORDER BY id;",
                this::mapToOrderItem);
    }
    /** BONUS!!! optional orderId query parameter!!!! */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return jdbcTemplate.query(
                "SELECT * FROM order_items WHERE order_id = ? ORDER BY id;",
                this::mapToOrderItem, orderId);
    }
    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM order_items WHERE id = ?",
                    this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public OrderItem createOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql,
                item.getOrderId(), item.getProductId(), item.getQuantity());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM order_items WHERE id = LAST_INSERT_ID();",
                this::mapToOrderItem);
    }
    public OrderItem updateOrderItem(OrderItem item) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?;";
        int rows = jdbcTemplate.update(sql,
                item.getOrderId(), item.getProductId(),
                item.getQuantity(), item.getId());
        if (rows == 0) {
            throw new DaoException("Zero rows touched, expected at least one.");
        }
        return getOrderItemById(item.getId());
    }
    public int deleteOrderItem(int id) {
        return jdbcTemplate.update("DELETE FROM order_items WHERE id = ?;", id);
    }
    private OrderItem mapToOrderItem(ResultSet rs, int rowNum) throws SQLException {
        return new OrderItem(
                rs.getInt("id"),
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity")
        );
    }
}