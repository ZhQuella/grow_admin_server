package dev.gad.common.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void usesDefaultPageRequestValues() {
        PageRequest request = new PageRequest();

        assertEquals(1, request.getPageNumber());
        assertEquals(20, request.getPageSize());
        assertEquals("/{pageNumber}/{pageSize}", PageRequest.PATH_PATTERN);
    }

    @Test
    void createsPageRequestFromPathVariables() {
        PageRequest request = PageRequest.of(2, 50);

        assertEquals(2, request.getPageNumber());
        assertEquals(50, request.getPageSize());
    }

    @Test
    void createsPageResultAndCalculatesTotalPages() {
        PageRequest request = new PageRequest(2, 20);

        PageResult<String> result = PageResult.of(List.of("record"), 21, request);

        assertEquals(List.of("record"), result.getRecords());
        assertEquals(21, result.getTotal());
        assertEquals(2, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void returnsZeroPagesWhenThereAreNoRecords() {
        PageResult<String> result = new PageResult<>(List.of(), 0, 1, 20);

        assertEquals(0, result.getTotalPages());
    }

    @Test
    void rejectsInvalidPaginationMetadata() {
        assertThrows(IllegalArgumentException.class,
                () -> new PageResult<>(List.of(), -1, 1, 20));
        assertThrows(IllegalArgumentException.class,
                () -> new PageResult<>(List.of(), 0, 0, 20));
        assertThrows(IllegalArgumentException.class,
                () -> new PageResult<>(List.of(), 0, 1, 0));
    }
}
