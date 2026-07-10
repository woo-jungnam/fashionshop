package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.UserProfileResponseDto;
import com.thaddeus.clothing.dto.UserProfileUpdateRequestDto;
import com.thaddeus.clothing.dto.UserAddressRequestDto;
import com.thaddeus.clothing.entity.UserAddress;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Quản lý thông tin hồ sơ tài khoản và sổ địa chỉ giao hàng của người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
            summary = "Lấy thông tin hồ sơ người dùng",
            description = "Lấy thông tin chi tiết tài khoản của người dùng dựa trên userId bao gồm họ tên, email, ngày sinh, số điện thoại, ảnh đại diện và vai trò (roles)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Truy xuất thông tin hồ sơ thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token JWT không hợp lệ/hết hạn",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thông tin tài khoản người dùng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ từ phía máy chủ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponseDto> getUserProfile(
            @Parameter(description = "ID duy nhất của người dùng cần tra cứu", required = true, example = "1")
            @RequestParam("userId") Long userId
    ) {
        UserProfileResponseDto response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Cập nhật hồ sơ người dùng",
            description = "Cập nhật các thông tin cơ bản của tài khoản người dùng như họ tên, số điện thoại, ngày sinh, giới tính và đường dẫn ảnh đại diện."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật hồ sơ tài khoản thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ (lỗi validate dữ liệu)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng cần cập nhật",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ từ phía máy chủ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @Parameter(description = "ID duy nhất của người dùng cần cập nhật", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody UserProfileUpdateRequestDto request
    ) {
        UserProfileResponseDto response = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/addresses")
    @Operation(
            summary = "Lấy danh sách sổ địa chỉ",
            description = "Trả về danh sách toàn bộ địa chỉ giao hàng đã được thiết lập bởi người dùng dựa trên userId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách địa chỉ thành công",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserAddress.class)))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserAddress>> getUserAddresses(
            @Parameter(description = "ID duy nhất của người dùng cần lấy danh sách địa chỉ", required = true, example = "1")
            @RequestParam("userId") Long userId
    ) {
        List<UserAddress> response = userService.getUserAddresses(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addresses")
    @Operation(
            summary = "Thêm địa chỉ giao hàng mới",
            description = "Thêm một địa chỉ nhận hàng mới vào sổ địa chỉ của người dùng. Nếu đánh dấu 'isDefault' là true, hệ thống tự động gỡ trạng thái mặc định của các địa chỉ cũ."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo mới địa chỉ giao hàng thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAddress.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu thông tin địa chỉ đầu vào không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserAddress> addUserAddress(
            @Parameter(description = "ID người dùng muốn thêm địa chỉ mới", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Valid @RequestBody UserAddressRequestDto request
    ) {
        UserAddress response = userService.addUserAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(
            summary = "Xóa địa chỉ giao hàng",
            description = "Xóa một địa chỉ giao hàng cụ thể dựa trên addressId và userId. Hệ thống kiểm tra quyền sở hữu địa chỉ của người dùng trước khi xóa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công, không có dữ liệu trả về"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu xóa không hợp lệ hoặc địa chỉ không thuộc về người dùng này",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ với ID được cung cấp",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUserAddress(
            @Parameter(description = "ID người dùng sở hữu địa chỉ", required = true, example = "1")
            @RequestParam("userId") Long userId,
            @Parameter(description = "ID của địa chỉ cần xóa", required = true, example = "10")
            @PathVariable("addressId") Long addressId
    ) {
        userService.deleteUserAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
