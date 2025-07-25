package com.techzen.techsale.repository;

import com.techzen.techsale.entity.PrivilegeEntity;
import com.techzen.techsale.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Integer> {

    Optional<PrivilegeEntity> findByUserId(String userId);

    final String PrivilegesList = "SELECT p.user_id, p.privileges, u.name, u.image_url " +
            "FROM privilege p " +
            "JOIN users u on u.id = p.user_id " +
            "WHERE p.privileges = :privileges " +
            "GROUP BY p.privileges, u.name";
    @Query(value = PrivilegesList, nativeQuery = true)
    List<Object[]> getAllApproveList( String privileges);

    @Query("SELECT privileges, COUNT(privileges) AS count FROM PrivilegeEntity GROUP BY privileges")
    List<String> getPrivileges();

}
