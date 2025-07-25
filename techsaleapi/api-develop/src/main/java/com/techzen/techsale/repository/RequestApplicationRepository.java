package com.techzen.techsale.repository;

import com.techzen.techsale.dto.RequestApplicationForListDetailDTO;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.enumeration.RoleEnum;
import com.techzen.techsale.enumeration.SortFieldRequestEnum;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.utils.SecurityUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RequestApplicationRepository {

    private final EntityManager entityManager;

    private final SecurityUtils securityUtils;

    private static final String SQL_COMMON =
            "FROM RequestApplicationEntity a " +
                    "LEFT JOIN UserEntity b on b.id = a.requestUserId " +
                    "LEFT JOIN UserEntity c on c.id = a.approverUserId " +
                    "LEFT JOIN UserEntity d on d.id = a.buyerUserId " +
                    "INNER JOIN ReasonEntity f on f.id = a.requestReason " +
                    "WHERE a.isDeleted = 0 " +
                    "AND (b.personnelType = 1 OR a.status != 'WAITING') " +
                    "AND NOT (b.isRemove = 1 AND a.status = 'WAITING') " +
                    "AND b.organization.id IN (:organizationIds)";



    private static final String SQL_SELECT_REQUEST =
            "SELECT new com.techzen.techsale.dto.RequestApplicationForListDetailDTO(" +
                    "a.id, a.requestReason, a.status, a.requestProductName, a.expectReceiveDate, " +
                    "a.createAt, a.requestUserId, a.approverUserId, b.name, b.avatarUrl, c.avatarUrl, c.name, d.id, d.name, d.avatarUrl, " +
                    "a.orderingDate, a.deliveryDate) " +
                    SQL_COMMON;


    private static final String SQL_COUNT_ELEMENTS =
        "SELECT COUNT(*) " + SQL_COMMON;

    private static final String SORT_BY_CREATE_AT_AND_EXPECT_RECEIVE_DATE = "cast(a.expectReceiveDate as LocalDate) , a.createAt DESC";

    public Long getTotalElements(SearchRequest searchRequest, boolean isAdmin, List<Long> organizationIds) {
        TypedQuery<Long> queryGet = getTypedQuery(new StringBuilder(SQL_COUNT_ELEMENTS),
            searchRequest,
            isAdmin, Long.class);
        queryGet.setParameter("organizationIds", organizationIds);
        return queryGet.getSingleResult();
    }

    public List<RequestApplicationForListDetailDTO> getRequestApplicationPage(
            SearchRequest searchRequest, boolean isAdmin, List<Long> organizationIds) {

        TypedQuery<RequestApplicationForListDetailDTO> queryGet =
                getTypedQuery(new StringBuilder(SQL_SELECT_REQUEST), searchRequest, isAdmin,
                        RequestApplicationForListDetailDTO.class);
        queryGet.setParameter("organizationIds", organizationIds);

        queryGet.setFirstResult(searchRequest.getPage() * searchRequest.getSize());
        queryGet.setMaxResults(searchRequest.getSize());
        return queryGet.getResultList();
    }

    private <T> TypedQuery<T> getTypedQuery(
        StringBuilder sqlQuery, SearchRequest searchRequest, boolean isAdmin, Class<T> clazz) {
        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();
        boolean isManager = userPrincipal.getAuthorities().stream().anyMatch(x -> RoleEnum.MANAGER.getCode().equals(x.getAuthority()));
        boolean isBuyer = userPrincipal.getAuthorities().stream().anyMatch(x -> RoleEnum.BUYER.getCode().equals(x.getAuthority()));
        String userId = (isManager || isBuyer) && isAdmin ? "" : userPrincipal.getId();
        Map<String, Object> params = new HashMap<>();
        String name = searchRequest.getName();
        LocalDate createAtBegin = searchRequest.getCreatedAtBegin();
        LocalDate createAtEnd = searchRequest.getCreatedAtEnd();
        LocalDate expectReceiveDateBegin = searchRequest.getExpectReceiveDateBegin();
        LocalDate expectReceiveDateEnd = searchRequest.getExpectReceiveDateEnd();
        String requestReason = searchRequest.getRequestReason();
        SortFieldRequestEnum sort = searchRequest.getSort();
        Sort.Direction direction = searchRequest.getDirection();
        List<RequestStatusEnum> statusList = searchRequest.getStatusList();

        addCondition(sqlQuery, params, "b.id = :userId", "userId", userId);


        addCondition(
            sqlQuery, params,
            " (b.name LIKE CONCAT('%', :name, '%') OR c.name LIKE CONCAT('%', :name, '%') "
                + "OR a.requestProductName LIKE CONCAT ('%', :name, '%'))  ",
            "name", name);

        addCondition(
            sqlQuery, params, " a.requestReason = :requestReason ",
            "requestReason", requestReason);

        if (!CollectionUtils.isEmpty(statusList)) {
            sqlQuery.append(" AND a.status IN (:status) ");
            params.put("status", statusList);
        }

        addCondition(
            sqlQuery,
            params,
            " a.createAt >= :createAtBegin ",
            "createAtBegin",
            createAtBegin == null ? null : createAtBegin.atStartOfDay());

        addCondition(
            sqlQuery,
            params,
            " a.createAt <= :createAtEnd ",
            "createAtEnd",
            createAtEnd == null ? null : createAtEnd.atTime(LocalTime.MAX));

        addCondition(
            sqlQuery,
            params,
            " a.expectReceiveDate >= :expectReceiveDateBegin ",
            "expectReceiveDateBegin",
            expectReceiveDateBegin == null ? null : expectReceiveDateBegin.atStartOfDay());

        addCondition(
            sqlQuery,
            params,
            " a.expectReceiveDate <= :expectReceiveDateEnd ",
            "expectReceiveDateEnd",
            expectReceiveDateEnd == null ? null : expectReceiveDateEnd.atTime(LocalTime.MAX));

        addSort(sort, sqlQuery, direction, isBuyer, isManager, isAdmin);

        TypedQuery<T> typedQuery =
            entityManager.createQuery(sqlQuery.toString(), clazz);

        params.keySet().forEach(key -> typedQuery.setParameter(key, params.get(key)));

        return typedQuery;
    }

    private void addCondition(
        StringBuilder sqlQuery,
        Map<String, Object> params,
        String condition,
        String paramName,
        Object paramValue) {

        if (paramValue != null && StringUtils.hasText(paramValue.toString())) {
            sqlQuery.append(" AND ").append(condition);
            params.put(paramName, paramValue);
        }
    }

    private void addSort(
        SortFieldRequestEnum sort,
        StringBuilder sqlQuery,
        Sort.Direction direction,
        boolean isBuyer,
        boolean isManager,
        boolean isAdmin) {

        if (sort == null || sort.equals(SortFieldRequestEnum.STATUS)) {

            addSortStatus(sqlQuery, isBuyer, isManager, isAdmin);

            if (sort == null) {
                sqlQuery.append(" , " + SORT_BY_CREATE_AT_AND_EXPECT_RECEIVE_DATE);
                sqlQuery.append(
                    ", CASE WHEN a.requestReason = 'NEW_EMPLOYEE' THEN 1"
                        + " WHEN a.requestReason = 'BIRTHDAY' THEN 2"
                        + " WHEN a.requestReason = 'OTHER' THEN 3"
                        + " ELSE 0 END ");
            } else if (direction.isAscending()) {
                sqlQuery.append(
                    " ASC , " + SORT_BY_CREATE_AT_AND_EXPECT_RECEIVE_DATE);
            } else {
                sqlQuery.append(" DESC , " + SORT_BY_CREATE_AT_AND_EXPECT_RECEIVE_DATE);
            }

        } else if (sort.equals(SortFieldRequestEnum.REQUEST_REASON)) {
            addSortReason(sqlQuery, direction);
        } else {
            sqlQuery.append(" ORDER BY ").append(sort.getFieldName());
            if (direction.isAscending()) {
                sqlQuery.append(" ASC");
            } else {
                sqlQuery.append(" DESC");
            }
        }
    }

    private void addSortStatus(StringBuilder sqlQuery, boolean isBuyer, boolean isManager,
        boolean isAdmin) {
        if (isAdmin) {
            if (isBuyer) {
                sqlQuery.append(
                    " ORDER BY CASE WHEN a.status = 'PROCESSING' THEN 1"
                        + " WHEN a.status = 'ORDERING' THEN 2"
                        + " WHEN a.status = 'WAITING' THEN 3"
                        + " WHEN a.status = 'SUCCESS' THEN 4"
                        + " WHEN a.status = 'REJECTED' THEN 5 ELSE 0 END");
            } else {
                sqlQuery.append(
                    " ORDER BY CASE WHEN a.status = 'WAITING' THEN 1"
                        + " WHEN a.status = 'PROCESSING' THEN 2"
                        + " WHEN a.status = 'ORDERING' THEN 3"
                        + " WHEN a.status = 'SUCCESS' THEN 4"
                        + " WHEN a.status = 'REJECTED' THEN 5 ELSE 0 END");
            }
        } else {
            sqlQuery.append(
                " ORDER BY CASE WHEN a.status = 'WAITING' THEN 1"
                    + " WHEN a.status = 'ORDERING' THEN 2"
                    + " WHEN a.status = 'PROCESSING' THEN 3"
                    + " WHEN a.status = 'SUCCESS' THEN 4"
                    + " WHEN a.status = 'REJECTED' THEN 5 ELSE 0 END");
        }
    }

    private void addSortReason(StringBuilder sqlQuery, Sort.Direction direction) {
        sqlQuery.append(
            " ORDER BY CASE WHEN a.requestReason = 'NEW_EMPLOYEE' THEN 1"
                + " WHEN a.requestReason = 'BIRTHDAY' THEN 2"
                + " WHEN a.requestReason = 'OTHER' THEN 3"
                + " ELSE 0 END ");
        if (direction.isAscending()) {
            sqlQuery.append(" ASC ");
        } else {
            sqlQuery.append(" DESC ");
        }
    }
}
