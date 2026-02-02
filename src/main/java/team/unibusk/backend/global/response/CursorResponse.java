package team.unibusk.backend.global.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CursorResponse<T>(

        List<T> content,

        LocalDateTime nextCursorTime,

        Long nextCursorId,

        boolean hasNext

) {

    public static <T> CursorResponse<T> of(
            List<T> content,
            LocalDateTime nextTime,
            Long nextId,
            boolean hasNext
    ) {
        return CursorResponse.<T>builder()
                .content(content)
                .nextCursorTime(nextTime)
                .nextCursorId(nextId)
                .hasNext(hasNext)
                .build();
    }

}
