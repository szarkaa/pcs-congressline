package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import hu.congressline.pcs.domain.OnlineRegistrationCustomAnswer;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.repository.OnlineRegistrationCustomAnswerRepository;
import hu.congressline.pcs.service.dto.OnlineRegCustomAnswerDTO;
import hu.congressline.pcs.service.dto.OnlineRegCustomAnswerReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OnlineRegCustomAnswerReportService extends XlsReportService {

    private final OnlineRegCustomQuestionRepository questionRepository;
    private final OnlineRegistrationCustomAnswerRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OnlineRegCustomAnswerReportDTO> findAll(Congress congress, String currency) {
        log.debug("Request to get all OnlineRegCustomAnswerReportDTO");
        final List<OnlineRegCustomQuestion> questionList = questionRepository.findAllByCongressIdAndCurrencyCurrencyOrderByQuestionOrder(congress.getId(), currency);
        final Map<Long, List<OnlineRegistrationCustomAnswer>> regAnswerMap = new HashMap<>();

        repository.findAllByRegistrationCongress(congress).stream().filter(a -> currency.equals(a.getQuestion().getCurrency().getCurrency())).forEach(answer -> {
            final Long registrationId = answer.getRegistration().getId();
            regAnswerMap.computeIfAbsent(registrationId, k -> new ArrayList<>());
            regAnswerMap.get(registrationId).add(answer);
        });

        final List<OnlineRegCustomAnswerReportDTO> resultList = new ArrayList<>();
        regAnswerMap.keySet().forEach(registrationId -> {
            List<OnlineRegistrationCustomAnswer> answerList = regAnswerMap.get(registrationId);
            OnlineRegCustomAnswerReportDTO dto = new OnlineRegCustomAnswerReportDTO();
            answerList.stream().findFirst().ifPresent(a -> {
                dto.setRegistrationId(a.getRegistration().getId());
                dto.setRegId(a.getRegistration().getRegId());
                dto.setName(a.getRegistration().getLastName() + ", " + a.getRegistration().getFirstName());
                dto.setCurrency(a.getQuestion().getCurrency().getCurrency());
            });

            List<String> answers = new ArrayList<>();
            questionList.forEach(question -> {
                final String answer = answerList.stream().filter(a -> a.getQuestion().equals(question))
                        .findFirst().map(OnlineRegistrationCustomAnswer::getAnswer).orElse("");
                answers.add(answer);
            });
            dto.setAnswers(answers);
            resultList.add(dto);
        });
        return resultList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress, String currency) throws IOException {
        final List<OnlineRegCustomAnswerReportDTO> resultList = findAll(congress, currency);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Name", 200);
        final List<OnlineRegCustomQuestion> questionList = questionRepository.findAllByCongressIdAndCurrencyCurrencyOrderByQuestionOrder(congress.getId(), currency);
        questionList.forEach(question -> columns.put(question.getQuestion(), 200));

        final XSSFSheet sheet = createXlsxTab(workbook, "Online registration custom question report", currency, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (OnlineRegCustomAnswerReportDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            int colIdx = 2;
            for (String answer : dto.getAnswers()) {
                addCell(row, wrappingCellStyle, colIdx, answer);
                colIdx++;
            }
            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the online registration custom question report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<OnlineRegCustomAnswerDTO> getAllCustomAnswersByRegId(Long registrationId) {
        final List<OnlineRegistrationCustomAnswer> answers = repository.findAllByRegistrationId(registrationId);
        answers.sort(Comparator.comparing(a -> a.getQuestion().getQuestionOrder()));
        final List<OnlineRegCustomAnswerDTO> resultList = new ArrayList<>();
        answers.forEach(answer -> {
            OnlineRegCustomAnswerDTO dto = new OnlineRegCustomAnswerDTO();
            dto.setQuestion(answer.getQuestion().getQuestion());
            dto.setAnswer(answer.getAnswer());
            resultList.add(dto);
        });
        return resultList;
    }
}
