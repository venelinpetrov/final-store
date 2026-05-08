package com.vpe.finalstore.discount.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.vpe.finalstore.discount.dtos.CouponDto;
import com.vpe.finalstore.discount.entities.Coupon;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CouponMapper {
	CouponDto toDto (Coupon coupon);
}
