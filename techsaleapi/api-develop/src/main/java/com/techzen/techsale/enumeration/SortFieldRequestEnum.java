package com.techzen.techsale.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum SortFieldRequestEnum {
    ID("a.id"),
    STATUS("a.status"),
    REQUESTED_USER_ID("a.requestUserId"),
    CREATE_AT("a.createAt"),
    EXPECT_RECEIVE_DATE("a.expectReceiveDate"),
    PRODUCT_NAME("a.requestProductName"),
    USER_REQUESTED_NAME("b.name"),
    USER_ASSIGNED_NAME("c.name"),
    REQUEST_REASON("a.requestReason");

    String fieldName;
}
