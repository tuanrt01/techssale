package com.techzen.techsale.mapper;

import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.RequestApplicationForListDetailDTO;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.entity.RequestApplicationEntity;
import com.techzen.techsale.entity.RequestApplicationHistoryEntity;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestApplicationMapper {

    @Mapping(target = "requestUserId", source = "requestUserId")
    @Mapping(target = "status", source = "status")
    RequestApplicationEntity mapToRequestEntity(InformationRequest informationRequest, String requestUserId,
        RequestStatusEnum status);

    @Mapping(target = "requestId", source = "entity.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderingDate", source = "entity.orderingDate")
    @Mapping(target = "deliveryDate", source = "entity.deliveryDate")
    RequestApplicationHistoryEntity mapToRequestHistory(RequestApplicationEntity entity);

    @Mapping(source = "entity.requestUserId", target = "userRequest.id")
    @Mapping(source = "entity.userRequestedName", target = "userRequest.name")
    @Mapping(source = "entity.userRequestAvatar", target = "userRequest.avatarUrl")
    @Mapping(source = "entity.approverId", target = "userAssign.id")
    @Mapping(source = "entity.approverName", target = "userAssign.name")
    @Mapping(source = "entity.approverAvatar", target = "userAssign.avatarUrl")
    @Mapping(source = "entity.buyerId", target = "buyer.id")
    @Mapping(source = "entity.buyerName", target = "buyer.name")
    @Mapping(source = "entity.buyerAvatar", target = "buyer.avatarUrl")
    @Mapping(source = "entity.orderingDate", target = "orderingDate")
    @Mapping(source = "entity.deliveryDate", target = "deliveryDate")
    RequestApplicationForListDTO mapToRequestDetailForList(RequestApplicationForListDetailDTO entity);
}
