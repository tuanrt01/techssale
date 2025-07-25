package com.techzen.techsale.injection;

import com.techzen.techsale.enumeration.RequestStatusEnum;

public interface IRequestApplicationInjection {
    int getId();

    String getRequestReason();

    RequestStatusEnum getStatus();

    String getProductName();

    String getExpectReceiveDate();

    String getCreatedAt();

    String getRequestUserId();

    String getUserAssignedId();

    String getUserRequestedName();

    String getUserRequestAvatar();

    String getUserAssignedAvatar();

    String getUserAssignedName();
}
