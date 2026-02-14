package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.PayingGroupRepository;
import hu.congressline.pcs.service.dto.GroupDiscountItemDTO;
import hu.congressline.pcs.service.dto.PayingGroupDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupDiscountItemService extends XlsReportService {

    private final PayingGroupService payingGroupService;
    @PersistenceContext
    private EntityManager entityManager;

    private final PayingGroupRepository payingGroupRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<GroupDiscountItemDTO> findAll(String meetingCode, Long payingGroupId, String chargeableItemType) {
        log.debug("Request to get all group discount items");
        final Query query = entityManager.createNativeQuery(composeQuery(meetingCode, payingGroupId, chargeableItemType));
        query.setHint("jakarta.persistence.cache.storeMode", "REFRESH");
        query.setHint("jakarta.persistence.cache.retrieveMode", "BYPASS");
        List<?> result = query.getResultList();
        List<GroupDiscountItemDTO> groupDiscountItems = new ArrayList<>();
        for (Object item : result) {
            GroupDiscountItemDTO groupDiscountItem = getBeanFromRow((Object[]) item);
            groupDiscountItems.add(groupDiscountItem);
        }
        return groupDiscountItems;
    }

    protected GroupDiscountItemDTO getBeanFromRow(Object[] row) {
        GroupDiscountItemDTO bean = new GroupDiscountItemDTO();
        bean.setId(((BigDecimal) row[0]).longValue());
        bean.setRegId((Integer) row[1]);
        bean.setChargeableItemId(((Long) row[2]).intValue());
        bean.setFirstName((String) row[3]);
        bean.setLastName((String) row[4]);
        bean.setChargeableItemType(ChargeableItemType.valueOf((String) row[5]));
        bean.setPayingGroup(row[6] != null ? new PayingGroupDTO(payingGroupService.getById((Long) row[6])) : null);
        bean.setPayingGroupItemName((String) row[7]);
        bean.setDateOfPayment(row[8] != null ? (LocalDate) row[8] : null);
        BigDecimal amount = (BigDecimal) row[9];
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        bean.setAmount(amount);
        bean.setInvoiceNumber((String) row[11]);
        bean.setHotelName((String) row[12]);
        bean.setRoomType((String) row[13]);
        bean.setRoomMates((String) row[14]);
        return bean;
    }

    @SuppressWarnings({"MultipleStringLiterals", "MethodLength"})
    protected String composeQuery(String meetingCode, Long payingGroupId, String chargeableItemType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select\n");
        stringBuilder.append("cast((@row_number \\:= @row_number + 1) AS decimal(10, 0)) id,\n");
        stringBuilder.append("q.c1,\n");
        stringBuilder.append("q.c2,\n");
        stringBuilder.append("q.c3,\n");
        stringBuilder.append("q.c4,\n");
        stringBuilder.append("q.c5,\n");
        stringBuilder.append("q.c6,\n");
        stringBuilder.append("q.c7,\n");
        stringBuilder.append("q.c8,\n");
        stringBuilder.append("q.c9,\n");
        stringBuilder.append("q.c10,\n");
        stringBuilder.append("q.c11,\n");
        stringBuilder.append("q.c12,\n");
        stringBuilder.append("q.c13,\n");
        stringBuilder.append("q.c14\n");
        stringBuilder.append("from (\n");
        stringBuilder.append("select\n");
        stringBuilder.append("r.reg_id c1,\n");
        stringBuilder.append("rrt.id c2,\n");
        stringBuilder.append("r.first_name c3,\n");
        stringBuilder.append("r.last_name c4,\n");
        stringBuilder.append("pgi.chargeable_item_type c5,\n");
        stringBuilder.append("pg.id c6,\n");
        stringBuilder.append("pgi.name c7,\n");
        stringBuilder.append("ci.date_of_group_payment c8,\n");
        stringBuilder.append("if (pgi.amount_value is not null, pgi.amount_value, pgi.amount_percentage / 100 * rrt.reg_fee * rrt.acc_people) c9,\n");
        stringBuilder.append("pg.name c10,\n");
        stringBuilder.append("(select if(i.storno = 0, i.invoice_number, NULL) from invoice i\n");
        stringBuilder.append("join invoice_paying_group ipg on ipg.invoice_id = i.id\n");
        stringBuilder.append("join group_discount_invoice_history gdih on gdih.invoice_id = i.id\n");
        stringBuilder.append("where gdih.chargeable_item_id = c2 and ipg.paying_group_id = c6 order by i.id desc limit 1) c11,\n");
        stringBuilder.append("'' c12,\n");
        stringBuilder.append("'' c13,\n");
        stringBuilder.append("'' c14\n");
        stringBuilder.append("from\n");
        stringBuilder.append("registration r\n");
        stringBuilder.append("inner join congress c on r.congress_id = c.id\n");
        stringBuilder.append("inner join registration_registration_type rrt on r.id = rrt.registration_id\n");
        stringBuilder.append("inner join chargeable_item ci on ci.id = rrt.id\n");
        stringBuilder.append("inner join paying_group_item pgi on pgi.id = rrt.paying_group_item_id\n");
        stringBuilder.append("inner join paying_group pg on pgi.paying_group_id = pg.id\n");
        stringBuilder.append("where\n");
        stringBuilder.append("c.meeting_code = '").append(meetingCode).append("'\n");

        stringBuilder.append("union\n");

        stringBuilder.append("select\n");
        stringBuilder.append("r.reg_id c1,\n");
        stringBuilder.append("rrr.id c2,\n");
        stringBuilder.append("r.first_name c3,\n");
        stringBuilder.append("r.last_name c4,\n");
        stringBuilder.append("pgi.chargeable_item_type c5,\n");
        stringBuilder.append("pg.id c6,\n");
        stringBuilder.append("pgi.name c7,\n");
        stringBuilder.append("ci.date_of_group_payment c8,\n");
        stringBuilder.append("if (pgi.amount_percentage is not null,\n");
        stringBuilder.append("pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n");
        stringBuilder.append("/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n");
        stringBuilder.append("if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n");
        stringBuilder.append("rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n");
        stringBuilder.append("/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n");
        stringBuilder.append(",pgi.amount_value)) c9,\n");
        stringBuilder.append("pg.name c10,\n");
        stringBuilder.append("(select if(i.storno = 0, i.invoice_number, NULL) from invoice i\n");
        stringBuilder.append("join invoice_paying_group ipg on ipg.invoice_id = i.id\n");
        stringBuilder.append("join group_discount_invoice_history gdih on gdih.invoice_id = i.id\n");
        stringBuilder.append("where gdih.chargeable_item_id = c2 and ipg.paying_group_id = c6 order by i.id desc limit 1) c11,\n");
        stringBuilder.append("h.name c12,\n");
        stringBuilder.append("rm.room_type c13,\n");
        stringBuilder.append("(select group_concat(concat_ws(',', concat(r.last_name, ' ', r.first_name))) from registration r\n");
        stringBuilder.append("join room_reservation_registration rrr on rrr.registration_id = r.id where rrr.room_reservation_id = rr.id and rrr.registration_id <> r.id) c14\n");
        stringBuilder.append("from\n");
        stringBuilder.append("registration r\n");
        stringBuilder.append("inner join congress c on r.congress_id = c.id\n");
        stringBuilder.append("inner join room_reservation_registration rrr on r.id = rrr.registration_id\n");
        stringBuilder.append("inner join chargeable_item ci on ci.id = rrr.id\n");
        stringBuilder.append("inner join room_reservation rr on rrr.room_reservation_id = rr.id\n");
        stringBuilder.append("inner join room rm on rr.room_id = rm.id\n");
        stringBuilder.append("inner join congress_hotel ch on rm.congress_hotel_id = ch.id\n");
        stringBuilder.append("inner join hotel h on ch.hotel_id = h.id\n");
        stringBuilder.append("inner join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n");
        stringBuilder.append("inner join paying_group pg on pgi.paying_group_id = pg.id\n");
        stringBuilder.append("where\n");
        stringBuilder.append("c.meeting_code = '").append(meetingCode).append("'\n");

        stringBuilder.append("union\n");

        stringBuilder.append("select\n");
        stringBuilder.append("r.reg_id c1,\n");
        stringBuilder.append("oos.id c2,\n");
        stringBuilder.append("r.first_name c3,\n");
        stringBuilder.append("r.last_name c4,\n");
        stringBuilder.append("pgi.chargeable_item_type c5,\n");
        stringBuilder.append("pg.id c6,\n");
        stringBuilder.append("pgi.name c7,\n");
        stringBuilder.append("ci.date_of_group_payment c8,\n");
        stringBuilder.append("if (pgi.amount_value is not null, pgi.amount_value, pgi.amount_percentage / 100 * os.price * oos.participant) c9,\n");
        stringBuilder.append("pg.name c10,\n");
        stringBuilder.append("(select if(i.storno = 0, i.invoice_number, NULL) from invoice i\n");
        stringBuilder.append("join invoice_paying_group ipg on ipg.invoice_id = i.id\n");
        stringBuilder.append("join group_discount_invoice_history gdih on gdih.invoice_id = i.id\n");
        stringBuilder.append("where gdih.chargeable_item_id = c2 and ipg.paying_group_id = c6 order by i.id desc limit 1) c11,\n");
        stringBuilder.append("'' c12,\n");
        stringBuilder.append("'' c13,\n");
        stringBuilder.append("'' c14\n");
        stringBuilder.append("from\n");
        stringBuilder.append("registration r\n");
        stringBuilder.append("inner join congress c on r.congress_id = c.id\n");
        stringBuilder.append("inner join ordered_optional_service oos on r.id = oos.registration_id\n");
        stringBuilder.append("inner join chargeable_item ci on ci.id = oos.id\n");
        stringBuilder.append("inner join optional_service os on oos.optional_service_id = os.id\n");
        stringBuilder.append("inner join paying_group_item pgi on pgi.id = oos.paying_group_item_id\n");
        stringBuilder.append("inner join paying_group pg on pgi.paying_group_id = pg.id\n");
        stringBuilder.append("where\n");
        stringBuilder.append("c.meeting_code = '").append(meetingCode).append("'\n");
        stringBuilder.append(") as q, (select @row_number\\:=0) as rownum\n");

        if (chargeableItemType != null || payingGroupId != null) {
            stringBuilder.append(" where ");
            StringBuilder sb = new StringBuilder();
            if (chargeableItemType != null) {
                sb.append(" c5 = '").append(chargeableItemType).append("'");
            }

            if (payingGroupId != null) {
                sb.append(!sb.isEmpty() ? " and " : "").append(" c6 = ").append(payingGroupId);
            }
            stringBuilder.append(sb);
        }

        return stringBuilder.toString();
    }
}
