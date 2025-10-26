package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelSummaryDTO implements Serializable {
    private List<HotelSummaryCellDTO> cells;
    private List<HotelSummaryColumnDTO> columns;
    private List<LocalDate> rows;

    public HotelSummaryDTO(List<HotelSummaryCellDTO> cells) {
        this.cells = cells;
        final Set<HotelSummaryColumnDTO> columnSet = new HashSet<>();

        this.cells.forEach(cell -> {
            columnSet.add(new HotelSummaryColumnDTO(cell));
        });

        columnSet.stream().sorted(Comparator.comparing(HotelSummaryColumnDTO::getRoomType));
        columns = new ArrayList<>(columnSet.size());
        columns.addAll(columnSet);

        this.rows = cells.stream().map(HotelSummaryCellDTO::getReservationDate).toList();
        this.rows.stream().sorted();
    }
}
