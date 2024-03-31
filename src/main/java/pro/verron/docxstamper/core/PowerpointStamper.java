package pro.verron.docxstamper.core;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.CTTextParagraph;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import pro.verron.docxstamper.api.OfficeStamper;
import pro.verron.docxstamper.api.OfficeStamperException;

import java.io.OutputStream;
import java.util.List;

public class PowerpointStamper
        implements OfficeStamper<PresentationMLPackage> {

    @Override
    public void stamp(
            PresentationMLPackage template,
            Object context,
            OutputStream outputStream
    )
            throws OfficeStamperException {
        Class<CTTextParagraph> ctTextParagraphClass = CTTextParagraph.class;
        List<CTTextParagraph> ctTextParagraphs = PowerpointCollector.collect(template,
                ctTextParagraphClass);
        for (CTTextParagraph paragraph : ctTextParagraphs) {
            PowerpointParagraph paragraph1 = new PowerpointParagraph(
                    paragraph);
            String string = paragraph1.asString();
            for (var variable : Placeholders.findVariables(string)) {
                var replacement = new CTRegularTextRun();
                var evaluationContext = new StandardEvaluationContext(context);
                var parserConfiguration = new SpelParserConfiguration();
                var parser = new SpelExpressionParser(parserConfiguration);
                var expression = parser.parseExpression(variable.content());
                var value = expression.getValue(evaluationContext);

                replacement.setT((String) value);
                paragraph1.replace(variable, replacement);
            }

        }
        try {
            template.save(outputStream);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
    }


}
