package com.techzen.techsale.repository;

import com.techzen.techsale.entity.UserTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Integer> {

    Optional<UserTokenEntity> findFirstByToken(String token);

    boolean existsByTokenAndDeletedIsFalse(String token);

    String QUERY_CHECK_USER_REMOVED = "SELECT IF(EXISTS(SELECT * FROM users WHERE email = :email && (is_remove = 1  || personnel_type != 1)), 'true', 'false')";
    @Query(value = QUERY_CHECK_USER_REMOVED, nativeQuery = true)
    boolean isUserRemoved(@Param("email") String email);
}
