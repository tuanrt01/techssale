package com.techzen.techsale.repository;

import com.techzen.techsale.entity.ReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonRepository extends JpaRepository<ReasonEntity,Integer> {

    @Query(value = "SELECT * FROM ts_reason r JOIN users u ON u.id = r.create_by WHERE r.reason LIKE %:reason% AND r.is_remove = 0 order by r.create_at DESC", nativeQuery = true)
    List<ReasonEntity> getAllListReasonAndSearch(@Param("reason") String reason);
}
