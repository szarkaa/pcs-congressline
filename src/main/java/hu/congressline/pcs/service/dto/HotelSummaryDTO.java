package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelSummaryDTO implements Serializable {
    private List<HotelSummaryCellDTO> cells;
    private List<HotelSummaryColumnDTO> columns;
    private Set<LocalDate> rows;

    public HotelSummaryDTO(List<HotelSummaryCellDTO> cells) {
        this.cells = cells;
        final Set<HotelSummaryColumnDTO> columnSet = new HashSet<>();

        this.cells.forEach(cell -> {
            columnSet.add(new HotelSummaryColumnDTO(cell));
        });

        columns = new ArrayList<>(columnSet.size());
        columns.addAll(columnSet);
        columns.sort(Comparator.comparing(HotelSummaryColumnDTO::getRoomType));
        this.rows = new TreeSet<>(cells.stream().map(HotelSummaryCellDTO::getReservationDate).toList());
    }
}
