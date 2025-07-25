package com.techzen.techsale.repository;

import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.dto.UserDetailDTO;
import com.techzen.techsale.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("SELECT new com.techzen.techsale.dto.UserDetailDTO(a.id, a.name, a.mattermostName, a.email, a.avatarUrl, a.password, c.codeName, c.name, d.privileges, a.deleted) "
        +  "FROM  UserEntity     a "
        +  "LEFT JOIN  RoleUserEntity  b ON b.userId = a.id "
        +  "LEFT JOIN  RoleEntity      c ON c.id = b.roleId "
        +  "LEFT JOIN  PrivilegeEntity d ON d.userId = a.id AND d.businessFunction = :businessFunction "
        +  "WHERE a.email = :email AND c.app.id = 2")
    Optional<UserDetailDTO> getUserInfo(String email, String businessFunction);

    @Query("SELECT new com.techzen.techsale.dto.UserDTO(a.id, a.name,a.avatarUrl) "
        +  "FROM  UserEntity      a "
        +  "JOIN  PrivilegeEntity b ON b.userId = a.id "
        +  "WHERE b.businessFunction = 'TECHSALE_FUNCTION' AND b.privileges like '%approve%' AND a.isRemove = false AND a.isGuest = true AND a.personnelType = 1")
    List<UserDTO> findAllUserAssign();

    @Query("SELECT new com.techzen.techsale.dto.UserDTO(a.id, a.name,a.avatarUrl) "
        +  "FROM  UserEntity a "
        +  "WHERE a.isRemove = false AND a.isGuest = true AND a.personnelType.id = 1 ")
    List<UserDTO> findAllBuyer();

    @Query("SELECT u.mattermostName FROM UserEntity u WHERE u.id = :id")
    String getUserMattermostName(String id);

    UserEntity findByEmail(String email);
}
