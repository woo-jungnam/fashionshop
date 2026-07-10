package com.thaddeus.clothing.controller;

import com.thaddeus.clothing.dto.WarehouseRequestDto;
import com.thaddeus.clothing.dto.WarehouseResponseDto;
import com.thaddeus.clothing.dto.WarehouseStockAdjustRequestDto;
import com.thaddeus.clothing.dto.WarehouseStockResponseDto;
import com.thaddeus.clothing.exception.ErrorResponse;
import com.thaddeus.clothing.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Warehouse",
        description = """
                ## Quản lý kho hàng & Nghiệp vụ xuất nhập tồn
                
                Hệ thống API quản lý danh sách kho và các giao dịch nhập xuất kho hàng:
                - **CRUD Kho hàng**: Tạo mới, cập nhật, xóa, tra cứu thông tin kho.
                - **Xem tồn kho**: Liệt kê chi tiết tồn kho (`physicalQty`, `allocatedQty`, `availableToSellQty`) của từng biến thể sản phẩm trong kho.
                - **Nhập kho (Inbound)**: Cộng thêm số lượng vào tồn kho thực tế và khả dụng.
                - **Xuất kho (Outbound)**: Trừ số lượng tồn kho thực tế và khả dụng (nếu đủ số lượng).
                
                **Quyền truy cập:**
                - Yêu cầu JWT (Vai trò quản trị viên - ADMIN)
                """
)
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(summary = "Tạo mới kho hàng", description = "Tạo mới một kho hàng trong hệ thống. **Yêu cầu quyền ADMIN.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo kho hàng thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WarehouseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Tên kho hàng đã tồn tại hoặc dữ liệu không hợp lệ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc không đủ quyền hạn",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WarehouseResponseDto> createWarehouse(@Valid @RequestBody WarehouseRequestDto request) {
        WarehouseResponseDto response = warehouseService.createWarehouse(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả kho hàng", description = "Trả về danh sách toàn bộ các kho hàng đang hoạt động. **Yêu cầu quyền ADMIN.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách kho hàng",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WarehouseResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses() {
        List<WarehouseResponseDto> response = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin kho hàng theo ID", description = "Trả về thông tin chi tiết của một kho hàng theo ID. **Yêu cầu quyền ADMIN.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin kho hàng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WarehouseResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng (WRH_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WarehouseResponseDto> getWarehouseById(
            @Parameter(description = "ID kho hàng cần tra cứu", required = true, example = "1")
            @PathVariable("id") Long id) {
        WarehouseResponseDto response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin kho hàng", description = "Cập nhật tên, địa chỉ hoặc loại kho của kho hàng theo ID. **Yêu cầu quyền ADMIN.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WarehouseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tên kho đã được sử dụng",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng (WRH_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WarehouseResponseDto> updateWarehouse(
            @Parameter(description = "ID kho hàng cần cập nhật", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody WarehouseRequestDto request) {
        WarehouseResponseDto response = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa kho hàng", description = "Xóa hoàn toàn thông tin kho hàng theo ID. **Yêu cầu quyền ADMIN.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa kho hàng thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng (WRH_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteWarehouse(
            @Parameter(description = "ID kho hàng cần xóa", required = true, example = "1")
            @PathVariable("id") Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/inventory")
    @Operation(
            summary = "Xem báo cáo tồn kho chi tiết",
            description = "Trả về danh sách toàn bộ sản phẩm biến thể và số lượng tồn kho tương ứng trong kho hàng. **Yêu cầu quyền ADMIN.**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách tồn kho",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WarehouseStockResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng (WRH_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<WarehouseStockResponseDto>> getWarehouseInventory(
            @Parameter(description = "ID kho hàng cần xem tồn kho", required = true, example = "1")
            @PathVariable("id") Long id) {
        List<WarehouseStockResponseDto> response = warehouseService.getWarehouseInventory(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/inbound")
    @Operation(
            summary = "Nhập kho (Inbound/Stock-In)",
            description = """
                    Thực hiện giao dịch nhập thêm hàng vào kho.
                    
                    - Cộng dồn số lượng thực tế (`physicalQty`) và khả dụng bán (`availableToSellQty`).
                    - Nếu sản phẩm biến thể này chưa từng có trong kho hàng, hệ thống sẽ tự khởi tạo bản ghi mới với số lượng tương ứng.
                    - **Yêu cầu quyền ADMIN.**
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nhập kho thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WarehouseStockResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng hoặc biến thể sản phẩm",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WarehouseStockResponseDto> inboundStock(@Valid @RequestBody WarehouseStockAdjustRequestDto request) {
        WarehouseStockResponseDto response = warehouseService.inboundStock(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/outbound")
    @Operation(
            summary = "Xuất kho (Outbound/Stock-Out)",
            description = """
                    Thực hiện giao dịch xuất hàng ra khỏi kho.
                    
                    - Trừ bớt số lượng thực tế (`physicalQty`) và khả dụng bán (`availableToSellQty`).
                    - Kiểm tra nếu số lượng tồn kho khả dụng hiện tại nhỏ hơn số lượng xuất, hệ thống sẽ từ chối và trả về lỗi `OUT_OF_STOCK` (INV_001).
                    - **Yêu cầu quyền ADMIN.**
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xuất kho thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WarehouseStockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Tồn kho không đủ để xuất hàng (INV_001)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy kho hàng, biến thể hoặc bản ghi tồn kho",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WarehouseStockResponseDto> outboundStock(@Valid @RequestBody WarehouseStockAdjustRequestDto request) {
        WarehouseStockResponseDto response = warehouseService.outboundStock(request);
        return ResponseEntity.ok(response);
    }
}
