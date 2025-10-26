package hu.congressline.pcs.service.dto;

import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelSummaryColumnDTO {
    private Long roomId;
    private String roomType;

    public HotelSummaryColumnDTO(HotelSummaryCellDTO cell) {
        this.roomId = cell.getRoomId();
        this.roomType = cell.getRoomType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotelSummaryColumnDTO that = (HotelSummaryColumnDTO) o;
        return Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
