package org.example.controllers;
import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/api/products")
@PreAuthorize("isAuthenticated()")
public class ProductController {
    @Autowired
    private ProductDao productDao;
    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }
    @GetMapping("/{id}")
    public Product get(@PathVariable int id) {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }
    @PutMapping("/{id}")
    public Product update(@PathVariable int id, @RequestBody Product product) {
        product.setId(id);
        try {
            return productDao.updateProduct(product);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        int rows = productDao.deleteProduct(id);
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return rows;
    }
}