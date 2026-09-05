package dev.gad.common.page;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(name = "PageResult", description = "统一分页查询结果")
public class PageResult<T> {

    @Schema(description = "当前页数据")
    private final List<T> records;
    @Schema(description = "数据总条数", example = "100")
    private final long total;
    @Schema(description = "当前页码", example = "1")
    private final long pageNumber;
    @Schema(description = "每页条数", example = "20")
    private final long pageSize;
    @Schema(description = "总页数", example = "5")
    private final long totalPages;

    public PageResult(List<T> records, long total, long pageNumber, long pageSize) {
        if (total < 0) {
            throw new IllegalArgumentException("total must not be negative");
        }
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than zero");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than zero");
        }
        this.records = List.copyOf(Objects.requireNonNull(records, "records must not be null"));
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(total, pageSize);
    }

    public static <T> PageResult<T> of(List<T> records, long total, PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "pageRequest must not be null");
        return new PageResult<>(
                records,
                total,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize());
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPages() {
        return totalPages;
    }

    private static long calculateTotalPages(long total, long pageSize) {
        return total == 0 ? 0 : ((total - 1) / pageSize) + 1;
    }
}
