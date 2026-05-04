package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


  List<Product> findByStockGreaterThan(Integer stock);

  List<Product> findByNameContainingIgnoreCase(String name);

  List<Product> findByCategory(String category);
}
