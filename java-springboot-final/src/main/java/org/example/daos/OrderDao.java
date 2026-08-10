package org.example.daos;
import org.example.exceptions.DaoException;
import org.example.models.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;
    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<Order> getOrders() {
        return jdbcTemplate.query(
                "SELECT * FROM orders ORDER BY id;",
                this::mapToOrder);
    }
    /** you know I'm using that username query parameter!! */
    public List<Order> getOrdersByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM orders WHERE username = ? ORDER BY id;",
                this::mapToOrder, username);
    }
    public Order getOrderById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM orders WHERE id = ?",
                    this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        jdbcTemplate.update(sql, order.getUsername());
        return jdbcTemplate.queryForObject(
                "SELECT * FROM orders WHERE id = LAST_INSERT_ID();",
                this::mapToOrder);
    }
    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?;";
        int rows = jdbcTemplate.update(sql,
                order.getUsername(), order.getId());
        if (rows == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        }
        return getOrderById(order.getId());
    }
    public int deleteOrder(int id) {
        return jdbcTemplate.update("DELETE FROM orders WHERE id = ?;", id);
    }
    private Order mapToOrder(ResultSet rs, int rowNum) throws SQLException {
        return new Order(rs.getInt("id"), rs.getString("username"));
    }
}