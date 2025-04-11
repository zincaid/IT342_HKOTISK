package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.OrderEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    OrderEntity findByOrderId(int orderId);

    List<OrderEntity> findAll();
}
