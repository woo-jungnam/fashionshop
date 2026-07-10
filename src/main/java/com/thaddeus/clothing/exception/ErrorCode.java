package com.thaddeus.clothing.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USR_001", "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    PRODUCT_VARIANT_NOT_FOUND("PRD_001", "Không tìm thấy biến thể sản phẩm", HttpStatus.NOT_FOUND),
    WAREHOUSE_NOT_FOUND("WRH_001", "Không tìm thấy kho hàng", HttpStatus.NOT_FOUND),
    SHIPPER_NOT_FOUND("SHP_001", "Không tìm thấy đơn vị vận chuyển", HttpStatus.NOT_FOUND),
    COUPON_NOT_FOUND("CPN_001", "Mã giảm giá không tồn tại hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    COUPON_LIMIT_EXCEEDED("CPN_002", "Bạn đã dùng hết lượt sử dụng của mã giảm giá này", HttpStatus.BAD_REQUEST),
    COUPON_MIN_ORDER_NOT_MET("CPN_003", "Giá trị đơn hàng chưa đạt hạn mức tối thiểu để áp dụng coupon", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK("INV_001", "Sản phẩm đã hết hàng hoặc số lượng tồn kho khả dụng không đủ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("ORD_001", "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    BRAND_NOT_FOUND("BRD_001", "Không tìm thấy thương hiệu", HttpStatus.NOT_FOUND),
    BRAND_ALREADY_EXISTS("BRD_002", "Thương hiệu đã tồn tại", HttpStatus.BAD_REQUEST),
    WAREHOUSE_ALREADY_EXISTS("WRH_002", "Kho hàng đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("USR_002", "Email này đã được đăng ký sử dụng", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("SYS_001", "Lỗi hệ thống nội bộ", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
