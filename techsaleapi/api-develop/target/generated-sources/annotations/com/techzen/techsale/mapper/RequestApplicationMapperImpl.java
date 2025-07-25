package com.techzen.techsale.mapper;

import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.RequestApplicationForListDetailDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.entity.RequestApplicationEntity;
import com.techzen.techsale.entity.RequestApplicationHistoryEntity;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T11:45:06+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class RequestApplicationMapperImpl implements RequestApplicationMapper {

    @Override
    public RequestApplicationEntity mapToRequestEntity(InformationRequest informationRequest, String requestUserId, RequestStatusEnum status) {
        if ( informationRequest == null && requestUserId == null && status == null ) {
            return null;
        }

        RequestApplicationEntity.RequestApplicationEntityBuilder requestApplicationEntity = RequestApplicationEntity.builder();

        if ( informationRequest != null ) {
            requestApplicationEntity.requestProductName( informationRequest.getRequestProductName() );
            requestApplicationEntity.productLink( informationRequest.getProductLink() );
            requestApplicationEntity.requestAmount( informationRequest.getRequestAmount() );
            requestApplicationEntity.estimatePrice( informationRequest.getEstimatePrice() );
            requestApplicationEntity.requestReason( informationRequest.getRequestReason() );
            requestApplicationEntity.requestReasonDetail( informationRequest.getRequestReasonDetail() );
            requestApplicationEntity.expectReceiveDate( informationRequest.getExpectReceiveDate() );
            requestApplicationEntity.description( informationRequest.getDescription() );
            requestApplicationEntity.approverUserId( informationRequest.getApproverUserId() );
            requestApplicationEntity.updatedAt( informationRequest.getUpdatedAt() );
            requestApplicationEntity.personnelUsed( informationRequest.getPersonnelUsed() );
            requestApplicationEntity.organizationUsed( informationRequest.getOrganizationUsed() );
        }
        requestApplicationEntity.requestUserId( requestUserId );
        requestApplicationEntity.status( status );

        return requestApplicationEntity.build();
    }

    @Override
    public RequestApplicationHistoryEntity mapToRequestHistory(RequestApplicationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RequestApplicationHistoryEntity.RequestApplicationHistoryEntityBuilder requestApplicationHistoryEntity = RequestApplicationHistoryEntity.builder();

        requestApplicationHistoryEntity.requestId( entity.getId() );
        requestApplicationHistoryEntity.orderingDate( entity.getOrderingDate() );
        requestApplicationHistoryEntity.deliveryDate( entity.getDeliveryDate() );
        requestApplicationHistoryEntity.requestUserId( entity.getRequestUserId() );
        requestApplicationHistoryEntity.requestProductName( entity.getRequestProductName() );
        requestApplicationHistoryEntity.requestAmount( entity.getRequestAmount() );
        requestApplicationHistoryEntity.requestReason( entity.getRequestReason() );
        requestApplicationHistoryEntity.requestReasonDetail( entity.getRequestReasonDetail() );
        requestApplicationHistoryEntity.expectReceiveDate( entity.getExpectReceiveDate() );
        requestApplicationHistoryEntity.approverUserId( entity.getApproverUserId() );
        requestApplicationHistoryEntity.buyerUserId( entity.getBuyerUserId() );
        requestApplicationHistoryEntity.approverRejectReason( entity.getApproverRejectReason() );
        requestApplicationHistoryEntity.buyerRejectReason( entity.getBuyerRejectReason() );
        requestApplicationHistoryEntity.boughtProductName( entity.getBoughtProductName() );
        requestApplicationHistoryEntity.boughtPrice( entity.getBoughtPrice() );
        requestApplicationHistoryEntity.boughtPlace( entity.getBoughtPlace() );
        requestApplicationHistoryEntity.boughtAmount( entity.getBoughtAmount() );
        requestApplicationHistoryEntity.isDeleted( entity.getIsDeleted() );
        requestApplicationHistoryEntity.status( entity.getStatus() );
        requestApplicationHistoryEntity.createdBy( entity.getCreatedBy() );
        requestApplicationHistoryEntity.createAt( entity.getCreateAt() );
        requestApplicationHistoryEntity.updatedBy( entity.getUpdatedBy() );
        requestApplicationHistoryEntity.updatedAt( entity.getUpdatedAt() );

        return requestApplicationHistoryEntity.build();
    }

    @Override
    public RequestApplicationForListDTO mapToRequestDetailForList(RequestApplicationForListDetailDTO entity) {
        if ( entity == null ) {
            return null;
        }

        RequestApplicationForListDTO.RequestApplicationForListDTOBuilder requestApplicationForListDTO = RequestApplicationForListDTO.builder();

        requestApplicationForListDTO.userRequest( requestApplicationForListDetailDTOToUserDTO( entity ) );
        requestApplicationForListDTO.userAssign( requestApplicationForListDetailDTOToUserDTO1( entity ) );
        requestApplicationForListDTO.buyer( requestApplicationForListDetailDTOToUserDTO2( entity ) );
        if ( entity.getOrderingDate() != null ) {
            requestApplicationForListDTO.orderingDate( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( entity.getOrderingDate() ) );
        }
        if ( entity.getDeliveryDate() != null ) {
            requestApplicationForListDTO.deliveryDate( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( entity.getDeliveryDate() ) );
        }
        requestApplicationForListDTO.id( entity.getId() );
        requestApplicationForListDTO.status( entity.getStatus() );
        if ( entity.getExpectReceiveDate() != null ) {
            requestApplicationForListDTO.expectReceiveDate( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( entity.getExpectReceiveDate() ) );
        }
        if ( entity.getCreatedAt() != null ) {
            requestApplicationForListDTO.createdAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( entity.getCreatedAt() ) );
        }
        requestApplicationForListDTO.requestReason( entity.getRequestReason() );
        requestApplicationForListDTO.productName( entity.getProductName() );

        return requestApplicationForListDTO.build();
    }

    protected UserDTO requestApplicationForListDetailDTOToUserDTO(RequestApplicationForListDetailDTO requestApplicationForListDetailDTO) {
        if ( requestApplicationForListDetailDTO == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( requestApplicationForListDetailDTO.getRequestUserId() );
        userDTO.name( requestApplicationForListDetailDTO.getUserRequestedName() );
        userDTO.avatarUrl( requestApplicationForListDetailDTO.getUserRequestAvatar() );

        return userDTO.build();
    }

    protected UserDTO requestApplicationForListDetailDTOToUserDTO1(RequestApplicationForListDetailDTO requestApplicationForListDetailDTO) {
        if ( requestApplicationForListDetailDTO == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( requestApplicationForListDetailDTO.getApproverId() );
        userDTO.name( requestApplicationForListDetailDTO.getApproverName() );
        userDTO.avatarUrl( requestApplicationForListDetailDTO.getApproverAvatar() );

        return userDTO.build();
    }

    protected UserDTO requestApplicationForListDetailDTOToUserDTO2(RequestApplicationForListDetailDTO requestApplicationForListDetailDTO) {
        if ( requestApplicationForListDetailDTO == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( requestApplicationForListDetailDTO.getBuyerId() );
        userDTO.name( requestApplicationForListDetailDTO.getBuyerName() );
        userDTO.avatarUrl( requestApplicationForListDetailDTO.getBuyerAvatar() );

        return userDTO.build();
    }
}
