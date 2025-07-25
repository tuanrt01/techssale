package com.techzen.techsale.repository;

import com.techzen.techsale.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    @Query(value = "WITH RECURSIVE organization_tree AS (" +
            "SELECT id FROM organization WHERE (id = :orgId OR :orgId = 0) AND del_flag = 0 " +
            "UNION ALL " +
            "SELECT o.id " +
            "FROM organization o " +
            "INNER JOIN organization_tree ot ON o.parent_id = ot.id " +
            "WHERE o.del_flag = 0" +
            ") " +
            "SELECT id FROM organization_tree",
            nativeQuery = true)
    List<Long> getAllChildOrganizationIds(@Param("orgId") Long orgId);

    @Query(value = "WITH RECURSIVE org_level(id, parent_id, level) AS (" +
            "SELECT id, parent_id, 1 AS level " +
            "FROM organization " +
            "WHERE parent_id IS NULL OR parent_id = 0 " +
            "UNION ALL " +
            "SELECT o.id, o.parent_id, ol.level + 1 " +
            "FROM organization o " +
            "INNER JOIN org_level ol ON o.parent_id = ol.id" +
            ") " +
            "SELECT level FROM org_level WHERE id = :orgId",
            nativeQuery = true)
    Integer getOrganizationLevel(@Param("orgId") Long orgId);

    String QUERY_FIND_PARENT_ORGANIZATION = "SELECT o FROM OrganizationEntity o where o.delFlag = 0";
    @Query(QUERY_FIND_PARENT_ORGANIZATION)
    List<OrganizationEntity> findByOrganizationParent();


} 