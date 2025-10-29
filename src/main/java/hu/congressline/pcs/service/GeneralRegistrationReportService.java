package hu.congressline.pcs.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.service.dto.GeneralRegistrationReportDTO;
import hu.congressline.pcs.web.rest.vm.GeneralRegistrationReportVM;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GeneralRegistrationReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CongressRepository congressRepository;
    private final RegistrationRegistrationTypeService rrtService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<GeneralRegistrationReportDTO> findAll(GeneralRegistrationReportVM reportFilter) {
        log.debug("Request to get all GeneralRegistrationReportDTO");
        final Query query = entityManager.createNativeQuery(composeQuery(reportFilter));
        List result = query.getResultList();
        final List<RegistrationRegistrationType> regTypesForCongress = rrtService.findAllByCongressId(Long.valueOf(reportFilter.getCongressId()));
        List<GeneralRegistrationReportDTO> list = new ArrayList<>();
        for (Object item : result) {
            GeneralRegistrationReportDTO dto = getBeanFromRow((Object[]) item, regTypesForCongress);
            list.add(dto);
        }
        return list;
    }

    private GeneralRegistrationReportDTO getBeanFromRow(Object[] row, List<RegistrationRegistrationType> regTypesForCongress) {
        GeneralRegistrationReportDTO bean = new GeneralRegistrationReportDTO();
        bean.setId(((BigInteger) row[0]).longValue());
        bean.setRegId((Integer) row[1]);
        bean.setLastName((String) row[2]);
        bean.setFirstName((String) row[3]);
        bean.setShortName((String) row[4]);
        bean.setInvoiceName((String) row[5]);
        bean.setTitle((String) row[6]);
        bean.setPosition((String) row[7]);
        bean.setOtherData((String) row[8]);
        bean.setDepartment((String) row[9]);
        bean.setZipCode((String) row[10]);
        bean.setCity((String) row[11]);
        bean.setStreet((String) row[12]);
        bean.setCountryCode((String) row[13]);
        bean.setCountry((String) row[14]);
        bean.setPhone((String) row[15]);
        bean.setEmail((String) row[16]);
        bean.setWorkplaceName((String) row[17]);
        bean.setWorkplaceDepartment((String) row[18]);
        bean.setWorkplaceZipCode((String) row[19]);
        bean.setWorkplaceCity((String) row[20]);
        bean.setWorkplaceStreet((String) row[21]);
        bean.setWorkplaceCountry((String) row[22]);
        bean.setWorkplacePhone((String) row[23]);
        bean.setWorkplaceEmail((String) row[24]);
        bean.setRemark((String) row[25]);
        bean.setDateOfApp(Instant.ofEpochMilli(((Date) row[26]).getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        bean.setAccompanyingNum(row[27] != null ? ((BigDecimal) row[27]).intValue() : 0);
        bean.setAccompanyingNames((String) row[28]);
        bean.setHotelNames((String) row[29]);
        bean.setRegistrationTypes(regTypesForCongress.stream().filter(rrt -> rrt.getRegistration().getId().equals(bean.getId()))
                .map(RegistrationRegistrationType::getRegistrationType).collect(Collectors.toList()));
        return bean;
    }

    @SuppressWarnings({"MethodLength", "MultipleStringLiterals"})
    private String composeWhereClause(GeneralRegistrationReportVM reportFilter) {
        StringBuilder stringBuilder = new StringBuilder();
        final String endline = "\n";
        stringBuilder.append("where r.congress_id = ").append(reportFilter.getCongressId()).append(endline);

        if (reportFilter.getRegId() != null) {
            stringBuilder.append("and r.reg_id = ").append(reportFilter.getRegId()).append(endline);
        }
        final String endLikeClause = "%'";
        if (StringUtils.isNotEmpty(reportFilter.getLastName())) {
            stringBuilder.append("and r.last_name like '%").append(reportFilter.getLastName()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getFirstName())) {
            stringBuilder.append("and r.first_name like '%").append(reportFilter.getFirstName()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getInvoiceName())) {
            stringBuilder.append("and r.invoice_name like '%").append(reportFilter.getInvoiceName()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getEmail())) {
            stringBuilder.append("and r.email like '%").append(reportFilter.getEmail()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getPosition())) {
            stringBuilder.append("and r.position like '%").append(reportFilter.getPosition()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getOtherData())) {
            stringBuilder.append("and r.other_data like '%").append(reportFilter.getOtherData()).append(endLikeClause).append(endline);
        }

        if (StringUtils.isNotEmpty(reportFilter.getAccPeopleLastName())) {
            stringBuilder.append("and rrt.id in (select ap.registration_registration_type_id from acc_people ap where ap.last_name like '%")
                    .append(reportFilter.getAccPeopleLastName()).append("%')\n");
        }

        if (StringUtils.isNotEmpty(reportFilter.getAccPeopleFirstName())) {
            stringBuilder.append("and rrt.id in (select ap.registration_registration_type_id from acc_people ap where ap.first_name like '%")
                    .append(reportFilter.getAccPeopleFirstName()).append("%')\n");
        }

        if (reportFilter.getRegistrationType() != null) {
            final String id = reportFilter.getRegistrationType().toString();
            stringBuilder.append("and rrt.registration_type_id = ").append(id).append(endline);
        }

        if (reportFilter.getWorkplace() != null) {
            final String id = reportFilter.getWorkplace().toString();
            stringBuilder.append("and w.id = ").append(id).append(endline);
        }

        if (reportFilter.getPayingGroup() != null) {
            final String id = reportFilter.getPayingGroup().toString();
            stringBuilder.append("and r.id in (\n");
            stringBuilder.append("select rrt.registration_id from registration_registration_type rrt\n");
            stringBuilder.append("join paying_group_item pgi on rrt.paying_group_item_id = pgi.id\n");
            stringBuilder.append("where pgi.paying_group_id = ").append(id).append(" union distinct\n");
            stringBuilder.append("select rrr.registration_id from room_reservation_registration rrr\n");
            stringBuilder.append("join paying_group_item pgi on rrr.paying_group_item_id = pgi.id\n");
            stringBuilder.append("where pgi.paying_group_id = ").append(id).append(" union distinct\n");
            stringBuilder.append("select oos.registration_id from ordered_optional_service oos\n");
            stringBuilder.append("join paying_group_item pgi on oos.paying_group_item_id = pgi.id\n");
            stringBuilder.append("where pgi.paying_group_id = ").append(id).append(")\n");
        }

        if (reportFilter.getOptionalService() != null) {
            final String id = reportFilter.getOptionalService().toString();
            stringBuilder.append("and oos.optional_service_id = ").append(id).append(endline);
        }

        if (reportFilter.getHotelId() != null) {
            final String id = reportFilter.getHotelId().toString();
            stringBuilder.append("and ch.hotel_id = ").append(id).append(endline);
        }

        if (reportFilter.getCountry() != null) {
            final String id = reportFilter.getCountry().toString();
            if (Boolean.TRUE.equals(reportFilter.getCountryNegation())) {
                stringBuilder.append("and (c.id <> ").append(id).append(" and cw.id <> ").append(id).append(")\n");
            } else {
                stringBuilder.append("and (c.id = ").append(id).append(" or cw.id = ").append(id).append(")\n");
            }
        }

        final String trueCondition = " = true)";
        if (reportFilter.getPresenter() != null) {
            final String str = "and (r.presenter" + (reportFilter.getPresenter() ? trueCondition : " = false or r.presenter is null)");
            stringBuilder.append(str).append(endline);
        }

        if (reportFilter.getEtiquette() != null) {
            final String str = "and (r.etiquette" + (reportFilter.getEtiquette() ? trueCondition : " = false or r.etiquette is null)");
            stringBuilder.append(str).append(endline);
        }

        if (reportFilter.getClosed() != null) {
            final String str = "and (r.closed" + (reportFilter.getClosed() ? trueCondition : " = false or r.closed is null)");
            stringBuilder.append(str).append(endline);
        }

        if (reportFilter.getOnSpot() != null) {
            final String str = "and (r.on_spot" + (reportFilter.getOnSpot() ? trueCondition : " = false or r.on_spot is null)");
            stringBuilder.append(str).append(endline);
        }

        if (reportFilter.getCancelled() != null) {
            final String str = "and (r.cancelled" + (reportFilter.getCancelled() ? trueCondition : " = false or r.cancelled is null)");
            stringBuilder.append(str + endline);
        }

        return stringBuilder.toString();
    }

    @SuppressWarnings("MethodLength")
    protected String composeQuery(GeneralRegistrationReportVM reportFilter) {
        return "select distinct\n"
                + "q.id,\n"
                + "q.reg_id,\n"
                + "q.last_name,\n"
                + "q.first_name,\n"
                + "q.short_name,\n"
                + "q.invoice_name,\n"
                + "q.title,\n"
                + "q.position,\n"
                + "q.other_data,\n"
                + "q.department,\n"
                + "q.zip_code,\n"
                + "q.city,\n"
                + "q.street,\n"
                + "q.country_code,\n"
                + "q.country,\n"
                + "q.phone,\n"
                + "q.email,\n"
                + "q.wp_name,\n"
                + "q.wp_department,\n"
                + "q.wp_zip_code,\n"
                + "q.wp_city,\n"
                + "q.wp_street,\n"
                + "q.wp_country,\n"
                + "q.wp_phone,\n"
                + "q.wp_email,\n"
                + "q.remark,\n"
                + "q.date_of_app,\n"
                + "q.acc_number,\n"
                + "q.acc_names,\n"
                + "q.hotel_names,\n"
                + "q.congress_id\n"
                + "from (\n"
                + "select\n"
                + "r.id,\n"
                + "r.reg_id,\n"
                + "r.last_name,\n"
                + "r.first_name,\n"
                + "r.short_name,\n"
                + "r.invoice_name,\n"
                + "r.title,\n"
                + "r.position,\n"
                + "r.other_data,\n"
                + "r.department,\n"
                + "r.zip_code,\n"
                + "r.city,\n"
                + "r.street,\n"
                + "c.code country_code,\n"
                + "c.name country,\n"
                + "r.phone,\n"
                + "r.email,\n"
                + "w.name wp_name,\n"
                + "w.department wp_department,\n"
                + "w.zip_code wp_zip_code,\n"
                + "w.city wp_city,\n"
                + "w.street wp_street,\n"
                + "cw.name wp_country,\n"
                + "w.phone wp_phone,\n"
                + "w.email wp_email,\n"
                + "r.remark,\n"
                + "r.date_of_app,\n"
                + "(select sum(rrt.acc_people) from registration_registration_type rrt\n"
                + "join registration_type rt on rrt.registration_type_id = rt.id\n"
                + "where rt.registration_type = 'ACCOMPANYING_FEE' and rrt.registration_id = r.id) acc_number,\n"
                + "(select group_concat(concat_ws(',', concat(ap.last_name, ' ', ap.first_name))) from acc_people ap\n"
                + "join registration_registration_type rrt on rrt.id = ap.registration_registration_type_id\n"
                + "where rrt.registration_id = r.id) acc_names,\n"
                + "(select group_concat(distinct concat_ws(',', h.name)) from hotel h\n"
                + "join congress_hotel ch on ch.hotel_id = h.id\n"
                + "join room rm on rm.congress_hotel_id = ch.id\n"
                + "join room_reservation rr on rr.room_id = rm.id\n"
                + "join room_reservation_registration rrr on rrr.room_reservation_id = rr.id\n"
                + "where rrr.registration_id = r.id) hotel_names,\n"
                + "r.congress_id\n"
                + "from registration r\n"
                + "left outer join registration_registration_type rrt on r.id = rrt.registration_id\n"
                + "left outer join ordered_optional_service oos on r.id = oos.registration_id\n"
                + "left outer join room_reservation_registration rrr on rrr.registration_id = r.id\n"
                + "left outer join room_reservation rr on rrr.room_reservation_id = rr.id\n"
                + "left outer join room rm on rr.room_id = rm.id\n"
                + "left outer join congress_hotel ch on rm.congress_hotel_id = ch.id\n"
                + "left outer join workplace w on w.id = r.workplace_id\n"
                + "left outer join country c on c.id = r.country_id\n"
                + "left outer join country cw on cw.id = w.country_id\n"
                + composeWhereClause(reportFilter)
                + ") q order by q.last_name, q.first_name\n";
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(String congressId, List<GeneralRegistrationReportDTO> dtos, boolean qrCodeIncluded) throws IOException, WriterException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Family Name", 200);
        columns.put("First Name", 200);
        columns.put("Short Name", 100);
        columns.put("Invoice Name", 100);
        columns.put("Title", 100);
        columns.put("Position", 100);
        columns.put("Other data", 100);
        columns.put("Department", 100);
        columns.put("ZipCode", 100);
        columns.put("City", 100);
        columns.put("Street", 200);
        columns.put("Country", 100);
        columns.put("Phone", 100);
        columns.put("Email", 100);
        columns.put("Workplace Name", 200);
        columns.put("Workplace Dept.", 200);
        columns.put("Workplace ZipCode", 100);
        columns.put("Workplace City", 100);
        columns.put("Workplace Street", 200);
        columns.put("Workplace Country", 100);
        columns.put("Workplace Phone", 100);
        columns.put("Workplace Email", 100);
        final List<String> regTypeColumns = dtos.stream().flatMap(dto -> dto.getRegistrationTypes().stream())
                .map(RegistrationType::getCode).distinct().sorted().collect(Collectors.toList());
        regTypeColumns.forEach(column -> columns.put("Reg. type: " + column, 100));
        columns.put("Accompanying no.", 200);
        columns.put("Accompanying names", 200);
        columns.put("Hotel names", 200);
        columns.put("Remark", 100);
        columns.put("Date Of App.", 100);
        if (qrCodeIncluded) {
            columns.put("QR code", 330);
        }

        Congress congress = congressRepository.findById(Long.valueOf(congressId))
                .orElseThrow(() -> new IllegalArgumentException("Congress not found with id: " + congressId));
        final XSSFSheet sheet = createXlsxTab(workbook, "General registration report", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (GeneralRegistrationReportDTO dto : dtos) {
            final XSSFRow row = sheet.createRow(rowIndex);
            if (qrCodeIncluded) {
                row.setHeight((short) 4800);
            }
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getLastName());
            addCell(row, wrappingCellStyle, 2, dto.getFirstName());
            addCell(row, wrappingCellStyle, 3, dto.getShortName());
            addCell(row, wrappingCellStyle, 4, dto.getInvoiceName());
            addCell(row, wrappingCellStyle, 5, dto.getTitle());
            addCell(row, wrappingCellStyle, 6, dto.getPosition());
            addCell(row, wrappingCellStyle, 7, dto.getOtherData());
            addCell(row, wrappingCellStyle, 8, dto.getDepartment());
            addCell(row, wrappingCellStyle, 9, dto.getZipCode());
            addCell(row, wrappingCellStyle, 10, dto.getCity());
            addCell(row, wrappingCellStyle, 11, dto.getStreet());
            addCell(row, wrappingCellStyle, 12, dto.getCountry());
            addCell(row, wrappingCellStyle, 13, dto.getPhone());
            addCell(row, wrappingCellStyle, 14, dto.getEmail());
            addCell(row, wrappingCellStyle, 15, dto.getWorkplaceName());
            addCell(row, wrappingCellStyle, 16, dto.getWorkplaceDepartment());
            addCell(row, wrappingCellStyle, 17, dto.getWorkplaceZipCode());
            addCell(row, wrappingCellStyle, 18, dto.getWorkplaceCity());
            addCell(row, wrappingCellStyle, 19, dto.getWorkplaceStreet());
            addCell(row, wrappingCellStyle, 20, dto.getWorkplaceCountry());
            addCell(row, wrappingCellStyle, 21, dto.getWorkplacePhone());
            addCell(row, wrappingCellStyle, 22, dto.getWorkplaceEmail());

            int colIdx = 23;
            for (String column : regTypeColumns) {
                addCell(row, wrappingCellStyle, colIdx++, dto.getRegistrationTypes().stream().anyMatch(rt -> column.equals(rt.getCode())) ? column : "");
            }
            addCell(row, wrappingCellStyle, colIdx++, dto.getAccompanyingNum());
            addCell(row, wrappingCellStyle, colIdx++, dto.getAccompanyingNames());
            addCell(row, wrappingCellStyle, colIdx++, dto.getHotelNames());
            addCell(row, wrappingCellStyle, colIdx++, dto.getRemark());
            addCell(row, wrappingCellStyle, colIdx++, dto.getDateOfApp());
            if (qrCodeIncluded) {
                insertImage(sheet, rowIndex, colIdx, generateQRCodeImage("REG-" + new DecimalFormat("00000").format(dto.getRegId())));
            }
            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, dtos.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the general registration report XLSX file", e);
            throw e;
        }
    }

    private BufferedImage generateQRCodeImage(String barcodeText) throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void insertImage(XSSFSheet sheet, int rowIndex, int columnIndex, BufferedImage imageBI) throws IOException {
        final CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        final Drawing drawing = sheet.createDrawingPatriarch();

        final ClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageBI, "png", baos);
        InputStream qrCodeIS = new ByteArrayInputStream(baos.toByteArray());

        final int result = sheet.getWorkbook().addPicture(qrCodeIS, Workbook.PICTURE_TYPE_PNG);
        anchor.setCol1(columnIndex);
        anchor.setRow1(rowIndex);

        final Picture picture = drawing.createPicture(anchor, result);
        //double scaleX = (double) imageBI.getWidth() / columnWidth * columnHeight / imageBI.getHeight();
        //contactInfo.resize(scaleX, 1);
        picture.resize();

        int pictWidthPx = picture.getImageDimension().width;
        float cellWidthPx = sheet.getColumnWidthInPixels(columnIndex);
        anchor.setDx1(Math.round(cellWidthPx / 2f - (float) pictWidthPx / 2f) * Units.EMU_PER_PIXEL);
        anchor.setDy1(12 * Units.EMU_PER_PIXEL);
        picture.resize();
    }

}
