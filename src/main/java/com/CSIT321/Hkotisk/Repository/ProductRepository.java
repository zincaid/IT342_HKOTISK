package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.ProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategory(String category);


    ProductEntity findByProductId(int productId);

    void deleteByProductId(int productId);

    List<ProductEntity> findAll();
}
