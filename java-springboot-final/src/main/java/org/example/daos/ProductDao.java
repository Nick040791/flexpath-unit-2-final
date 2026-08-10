

package org.example.daos;

//We need exceptions, model, spring (stereotype component), jdbc template, and our datasource imported
import org.example.exceptions.DaoException;
import org.example.models.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

//Imports for datasource, result set, exceptions, and list
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProductDao {
    //We want to use this class to provide access to the Product data table
    private final JdbcTemplate jdbcTemplate;

    public ProductDao(DataSource dataSource) {
        //use the datasource input to create a new JDBC template
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //List all products sorted by id
    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products ORDER BY id;", this::mapToProduct);
    }

    //grab the product using its id
    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //create a product method
    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?,?)";
        jdbcTemplate.update(sql, product.getName(), product.getPrice());
        return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = LAST_INSERT_ID();", this::mapToProduct);
    }

    //update a product method
    public Product updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?;";
        int rows = jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());
        if (rows == 0) {
            throw new DaoException("Zero rows touched, expected at least one row");
        }
        return getProductById(product.getId());
    }

    //delete a product method
    public int deleteProduct(int id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?;", id);

    }

    private Product mapToProduct(ResultSet resultSet, int rowNum) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price")
        );
    }
}