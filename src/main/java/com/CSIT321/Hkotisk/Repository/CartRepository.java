package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.CartEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByEmail(String email);

    List<CartEntity> findAllByEmailAndIsOrderedFalse(String email);

    Optional<CartEntity> findByEmailAndProductIdAndProductSizeAndIsOrderedFalse(String email, int productId, String productSize);

    CartEntity findByCartIdAndEmail(int cartId, String email);

    void deleteByCartIdAndEmail(int cartId, String email);

    List<CartEntity> findAllByEmail(String email);

    List<CartEntity> findAllByOrderId(int orderId);
}
