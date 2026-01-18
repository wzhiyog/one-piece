package com.example.spring.boot.learning.agreement;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class FillService {
    private final DataLoaderFactory dataLoaderFactory;
    private final ExpressionParser parser = new SpelExpressionParser();

    public byte[] fillPdf(FillContext fillContext) {
        // 获取填充项
        List<FillItem> fillItemList = fillContext.getFillItemList();

        // 加载数据源
        Map<String, Object> dataMap = loadData(fillContext, fillItemList);

        // 解析字段
        Map<String, String> formData = parse(dataMap, fillItemList);

        // 加载模板
        byte[] pdf = loadPdf();

        // 填充pdf
        return fillPdf(formData, pdf);
    }

    private byte[] loadPdf() {
        try {
            return Files.readAllBytes(Paths.get("C:\\Users\\wuzhi\\Desktop\\test.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] fillPdf(Map<String, String> formData, byte[] pdf) {
//        try(PDDocument document = Loader.loadPDF(pdf)){
//            PDDocumentCatalog documentCatalog = document.getDocumentCatalog();
//            PDAcroForm acroForm = documentCatalog.getAcroForm();
//            if(acroForm == null){
//                return null;
//            }
//            Iterator<PDField> formFieldIterator = acroForm.getFieldIterator();
//            while (formFieldIterator.hasNext()) {
//                PDField formField = formFieldIterator.next();
//                String formKey = formField.getFullyQualifiedName();
//                String fillValue = formData.get(formKey);
//                log.info("fill field: {}, value: {}", formKey, fillValue);
//                formField.setValue(fillValue);
//                formField.setReadOnly(true);
//            }
//            acroForm.flatten(); // 锁定表单
//            // 4. 保存结果
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            document.save(output);
//            return output.toByteArray();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        PdfReader reader = null;
        PdfStamper ps = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            reader = new PdfReader(pdf);
            /* 将要生成的目标PDF文件名称 */
            ps = new PdfStamper(reader, bos);
            /* 使用中文字体 */
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            ArrayList<BaseFont> fontList = new ArrayList<>();
            fontList.add(bf);
            /* 取出报表模板中的所有字段 */
            AcroFields fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);
            for (Map.Entry<String, AcroFields.Item> entry : fields.getFields().entrySet()) {
                String key = entry.getKey();
                String value = formData.get(key);
                fields.setField(key, value);
                log.info("fill field: {}, value: {}", key, value);
            }
            /* 必须要调用这个，否则文档不会生成的 如果为false那么生成的PDF文件还能编辑，一定要设为true */
            ps.setFormFlattening(true);
            ps.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                // ignore
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }

    }

    private Map<String, String> parse(Map<String, Object> dataMap, List<FillItem> fillItemList) {
        if (CollectionUtils.isEmpty(fillItemList) || CollectionUtils.isEmpty(dataMap)) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Map<String, String> formData = (Map<String, String>) dataMap.getOrDefault(DataLoaderEnum.FORM.name(), new HashMap<>());
        EvaluationContext evaluationContext = null;
        for (FillItem fillItem : fillItemList) {
            FillTypeEnum fillType = fillItem.getFillType();
            String value = null;
            switch (fillType) {
                case EMPTY:
                    value = StringUtils.EMPTY;
                    break;

                case FIXED:
                    value = fillItem.getExpression();
                    break;

                case EXPRESSION:
                    if (evaluationContext == null) {
                        evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding()
                                .withRootObject(dataMap)
                                .build();
                    }
                    value = parseExpression(fillItem, evaluationContext);
                    if (value == null) continue;
                    break;
            }
            log.debug("parse fill item: {}, value: {}, fillType: {}, expression:{}", fillItem, value, fillType, fillItem.getExpression());
            formData.put(fillItem.getItemName(), value);
            dataMap.put(DataLoaderEnum.FORM.name(), formData);
        }
        return formData;
    }

    private String parseExpression(FillItem fillItem, EvaluationContext evaluationContext) {
        String expression = String.format("[%s].%s", fillItem.getDataLoader().name(), fillItem.getExpression());
        Expression exp = parser.parseExpression(expression);
        Object value = exp.getValue(evaluationContext);
        if (value == null) {
            return null;
        }
        if (fillItem.getFormatter() == null) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).stripTrailingZeros().toPlainString();
            }
            return value.toString();
        }
        switch (fillItem.getFormatter()) {
            case DATETIME:
                if (value instanceof Date) {
                    return new SimpleDateFormat(fillItem.getFormatPattern()).format(value);
                }
                if (value instanceof Temporal) {
                    return new DateTimeFormatterFactory(fillItem.getFormatPattern()).createDateTimeFormatter().format((Temporal) value);
                }
            case NUMBER:
                if (value instanceof String && NumberUtils.isCreatable((String) value)) {
                    value = new BigDecimal((String) value);
                }
                DecimalFormat decimalFormat = new DecimalFormat(fillItem.getFormatPattern());
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                return decimalFormat.format(value);
        }
        return value.toString();
    }

    private Map<String, Object> loadData(FillContext fillContext, List<FillItem> fillItemList) {
        if (CollectionUtils.isEmpty(fillItemList)) {
            return Collections.emptyMap();
        }

        Map<String, Object> dataMap = new HashMap<>();
        for (FillItem fillItem : fillItemList) {
            DataLoaderEnum dataLoader = fillItem.getDataLoader();
            if (dataMap.containsKey(dataLoader.name())) {
                continue;
            }

            dataMap.put(dataLoader.name(), dataLoaderFactory.getDataLoader(dataLoader).loadData(fillContext));
        }
        return dataMap;
    }
}
