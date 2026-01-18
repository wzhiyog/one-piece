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
public class ContractService {
    private final DataLoaderFactory dataLoaderFactory;
    private final ExpressionParser parser = new SpelExpressionParser();

    public byte[] createContract(ContractReq contractReq) {
        // 获取填充项
        List<FillItem> fillItemList = getFillItemList(contractReq);

        // 加载数据源
        List<Integer> dataLoaderList = fillItemList.stream().map(FillItem::getDataLoader).distinct().toList();
        Map<Integer, Object> dataSourceMap = loadDataSource(contractReq, dataLoaderList);

        // 解析字段
        Map<String, String> formData = parseItem(dataSourceMap, fillItemList);

        // 加载模板
        byte[] pdf = loadTemplate();

        // 填充pdf
        return fillTemplate(formData, pdf);
    }

    private List<FillItem> getFillItemList(ContractReq contractReq) {
        FillItem fillItem = new FillItem();
        fillItem.setItemName("NameB");
        fillItem.setExpression("date");
        fillItem.setFillType(FillTypeEnum.EXPRESSION.getCode());
        fillItem.setDataLoader(DataLoaderEnum.DATA1.getCode());
        fillItem.setOrder(1);
        fillItem.setFormatter(FormatterEnum.DATETIME.getCode());
        fillItem.setFormatPattern("yyyy-MM-dd HHmmss");

        FillItem fillItem2 = new FillItem();
        fillItem2.setItemName("NameA");
        fillItem2.setExpression("numberString");// map要用中括号
        fillItem2.setFillType(FillTypeEnum.EXPRESSION.getCode());
        fillItem2.setDataLoader(DataLoaderEnum.DATA1.getCode());
        fillItem2.setOrder(2);
        fillItem2.setFormatter(FormatterEnum.NUMBER.getCode());
        fillItem2.setFormatPattern("¤#.##");
        return Arrays.asList(fillItem, fillItem2);
    }

    private byte[] loadTemplate() {
        try {
            return Files.readAllBytes(Paths.get("C:\\Users\\wuzhi\\Desktop\\test.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] fillTemplate(Map<String, String> formData, byte[] pdf) {
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

    private Map<String, String> parseItem(Map<Integer, Object> dataSourceMap, List<FillItem> fillItemList) {
        if (CollectionUtils.isEmpty(fillItemList) || CollectionUtils.isEmpty(dataSourceMap)) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        Map<String, String> formData = (Map<String, String>) dataSourceMap.getOrDefault(DataLoaderEnum.FORM.getCode(), new HashMap<>());
        EvaluationContext evaluationContext = null;
        for (FillItem fillItem : fillItemList) {
            FillTypeEnum fillType = Objects.requireNonNull(FillTypeEnum.getByCode(fillItem.getFillType()), "fillType not found: " + fillItem.getFillType());
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
                                .withRootObject(dataSourceMap)
                                .build();
                    }
                    value = parseExpression(fillItem, evaluationContext);
                    if (value == null) continue;
                    break;
            }
            log.info("parseItem fill item: {}, value: {}, fillType: {}, expression: {}", fillItem.getItemName(), value, fillType, fillItem.getExpression());
            formData.put(fillItem.getItemName(), value);
        }
        return formData;
    }

    private String parseExpression(FillItem fillItem, EvaluationContext evaluationContext) {
        DataLoaderEnum dataLoaderEnum = Objects.requireNonNull(DataLoaderEnum.getByCode(fillItem.getDataLoader()), "data loader not found: " + fillItem.getDataLoader());
        String expression = String.format("[%s].%s", dataLoaderEnum.getCode(), fillItem.getExpression());
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
        FormatterEnum formatterEnum = Objects.requireNonNull(FormatterEnum.getByCode(fillItem.getFormatter()), "formatter not found: " + fillItem.getFormatter());
        switch (formatterEnum) {
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

    private Map<Integer, Object> loadDataSource(ContractReq contractReq, List<Integer> dataLoaderList) {
        if (CollectionUtils.isEmpty(dataLoaderList)) {
            return Collections.emptyMap();
        }

        Map<Integer, Object> dataMap = new HashMap<>();
        for (Integer dataLoader : dataLoaderList) {
            DataLoaderEnum dataLoaderEnum = Objects.requireNonNull(DataLoaderEnum.getByCode(dataLoader), "data loader not found: " + dataLoader);
            dataMap.put(dataLoader, dataLoaderFactory.getDataLoader(dataLoaderEnum).loadData(contractReq));
        }
        return dataMap;
    }
}
