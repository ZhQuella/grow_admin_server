package dev.gad.common.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "PageRequest", description = "统一分页请求参数")
public class PageRequest {

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 200;
    public static final String PAGE_NUMBER_PATH_VARIABLE = "pageNumber";
    public static final String PAGE_SIZE_PATH_VARIABLE = "pageSize";
    public static final String PATH_PATTERN = "/{" + PAGE_NUMBER_PATH_VARIABLE
            + "}/{" + PAGE_SIZE_PATH_VARIABLE + "}";

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    @Schema(description = "页码，从1开始", example = "1", minimum = "1")
    private Integer pageNumber = DEFAULT_PAGE_NUMBER;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = MAX_PAGE_SIZE, message = "每页条数不能超过200")
    @Schema(description = "每页条数，最大200", example = "20", minimum = "1", maximum = "200")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    public PageRequest() {
    }

    public PageRequest(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static PageRequest of(Integer pageNumber, Integer pageSize) {
        return new PageRequest(pageNumber, pageSize);
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
