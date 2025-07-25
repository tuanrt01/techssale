package com.techzen.techsale.repository;

import com.techzen.techsale.dto.dashboard.IDashBoardDTO;
import com.techzen.techsale.entity.RequestApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDashBoardRepository extends JpaRepository<RequestApplicationEntity, Integer> {


    @Query(value = "SELECT " +
            "    SUM(ts.boughtPrice * ts.boughtAmount) AS total_value " +
            "FROM " +
            "    RequestApplicationEntity ts " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "AND YEAR(ts.createAt) = :year " +
            "GROUP BY " +
            "    YEAR(ts.createAt) ")
    Long getTotalPriceInYear(Integer year);

    @Query(value = "SELECT " +
            "    r.reason as reason, " +
            "    SUM(ts.bought_price * ts.bought_amount) AS total " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN ts_reason r ON r.id = ts.reason_request " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "AND YEAR(ts.created_at) = :year " +
            "GROUP BY " +
            "    ts.reason_request " +
            "ORDER BY " +
            "    total DESC ", nativeQuery = true)
    List<IDashBoardDTO> getReasonBest(Integer year);

    @Query(value = "SELECT " +
            "    r.reason as reason, " +
            "    SUM(ts.bought_price * ts.bought_amount) AS total " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN ts_reason r ON r.id = ts.reason_request " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "AND YEAR(ts.created_at) = :year " +
            "AND MONTH(ts.created_at) = :month " +
            "GROUP BY " +
            "    ts.reason_request " +
            "ORDER BY " +
            "    total DESC ", nativeQuery = true)
    List<IDashBoardDTO> getReasonBestByMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT " +
            "    YEAR(ts.createAt) AS year, " +
            "    MONTH(ts.createAt) AS month, " +
            "    SUM(ts.boughtPrice * ts.boughtAmount) AS total " +
            "FROM " +
            "    RequestApplicationEntity ts " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "AND YEAR(ts.createAt) = :year " +
            "GROUP BY " +
            "    YEAR(ts.createAt), MONTH(ts.createAt) " +
            "ORDER BY " +
            "    total DESC ")
    List<IDashBoardDTO> getMonthBest(Integer year);


    @Query("SELECT " +
            "    u.name as user, " +
            "    SUM(ts.boughtPrice * ts.boughtAmount) AS total, " +
            "    COUNT(*) AS quantity, " +
            "    u.id as id " +
            "FROM " +
            "    RequestApplicationEntity ts " +
            "JOIN UserEntity u on u.id = ts.requestUserId " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
//            " AND YEAR(ts.createAt) = YEAR(CURRENT_DATE) " +
            " AND YEAR(ts.createAt) = :year " +
            "GROUP BY " +
            "    ts.createdBy " +
            "ORDER BY " +
            "    total DESC ")
    List<IDashBoardDTO> getUserBest(Integer year);
    
    @Query("SELECT " +
            "    u.name as user, " +
            "    SUM(ts.boughtPrice * ts.boughtAmount) AS total, " +
            "    COUNT(*) AS quantity, " +
            "    u.id as id " +
            "FROM " +
            "    RequestApplicationEntity ts " +
            "JOIN UserEntity u on u.id = ts.requestUserId " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            " AND YEAR(ts.createAt) = :year " +
            " AND MONTH(ts.createAt) = :month " +
            "GROUP BY " +
            "    ts.createdBy " +
            "ORDER BY " +
            "    total DESC ")
    List<IDashBoardDTO> getUserBestByMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query(value = "SELECT " +
            "    u.name as user, " +
            "    ts.bought_product_name as product, " +
            "    (ts.bought_price * ts.bought_amount) as amount, " +
            "    ts.created_at as date " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN users u ON u.id = ts.request_user_id " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "    AND YEAR(ts.created_at) = :year " +
            "    AND MONTH(ts.created_at) = :month " +
            "ORDER BY " +
            "    ts.created_at DESC", nativeQuery = true)
    List<Object[]> findExpenseDetailsByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * Lấy chi tiết chi tiêu theo quý và năm
     */
    @Query(value = "SELECT " +
            "    u.name as user, " +
            "    ts.bought_product_name as product, " +
            "    (ts.bought_price * ts.bought_amount) as amount, " +
            "    ts.created_at as date " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN users u ON u.id = ts.request_user_id " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "    AND YEAR(ts.created_at) = :year " +
            "    AND QUARTER(ts.created_at) = :quarter " +
            "ORDER BY " +
            "    ts.created_at DESC", nativeQuery = true)
    List<Object[]> findExpenseDetailsByQuarter(@Param("year") Integer year, @Param("quarter") Integer quarter);

    @Query(value = "SELECT " +
            "    u.name as user, " +
            "    ts.bought_product_name as product, " +
            "    (ts.bought_price * ts.bought_amount) as amount, " +
            "    ts.created_at as date " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN users u ON u.id = ts.request_user_id " +
            "JOIN ts_reason r ON r.id = ts.reason_request " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "    AND YEAR(ts.created_at) = :year " +
            "    AND r.reason = :reason " +
            "ORDER BY " +
            "    ts.created_at DESC", nativeQuery = true)
    List<Object[]> findExpenseDetailsByReason(@Param("year") Integer year, @Param("reason") String reason);

    @Query(value = "SELECT " +
            "    u.name as user, " +
            "    ts.bought_product_name as product, " +
            "    (ts.bought_price * ts.bought_amount) as amount, " +
            "    ts.created_at as date " +
            "FROM " +
            "    ts_request_application ts " +
            "JOIN users u ON u.id = ts.request_user_id " +
            "WHERE " +
            "    ts.status = 'SUCCESS' " +
            "    AND YEAR(ts.created_at) = :year " +
            "    AND ts.request_user_id = :userId " +
            "ORDER BY " +
            "    ts.created_at DESC", nativeQuery = true)
    List<Object[]> findExpenseDetailsByUser(@Param("year") Integer year, @Param("userId") String userId);
}
