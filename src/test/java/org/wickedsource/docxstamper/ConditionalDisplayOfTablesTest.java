package org.wickedsource.docxstamper;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.junit.Assert;
import org.junit.Test;
import org.wickedsource.docxstamper.docx4j.AbstractDocx4jTest;
import org.wickedsource.docxstamper.docx4j.RunAggregator;
import org.wickedsource.docxstamper.docx4j.TestContext;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;

public class ConditionalDisplayOfTablesTest extends AbstractDocx4jTest {

    @Test
    public void test() throws Docx4JException, IOException {
        TestContext context = new TestContext();
        context.setName("Homer");
        InputStream template = getClass().getResourceAsStream("ConditionalDisplayOfTablesTest.docx");
        WordprocessingMLPackage document = stampAndLoad(template, context);
        globalTablesAreRemoved(document);
        nestedTablesAreRemoved(document);
    }

    private void globalTablesAreRemoved(WordprocessingMLPackage document) {
        P p1 = (P) document.getMainDocumentPart().getContent().get(1);
        Tbl table2 = (Tbl) ((JAXBElement) document.getMainDocumentPart().getContent().get(3)).getValue();
        Tbl table3 = (Tbl) ((JAXBElement) document.getMainDocumentPart().getContent().get(5)).getValue();
        P p4 = (P) document.getMainDocumentPart().getContent().get(7);

        Assert.assertEquals("This paragraph stays untouched.", new RunAggregator(p1).getText());
        Assert.assertNotNull(table2);
        Assert.assertNotNull(table3);
        Assert.assertEquals("This paragraph stays untouched.", new RunAggregator(p4).getText());
    }

    private void nestedTablesAreRemoved(WordprocessingMLPackage document) {
        Tbl outerTable = (Tbl) ((JAXBElement) document.getMainDocumentPart().getContent().get(3)).getValue();
        Tc cell = (Tc) ((JAXBElement) ((Tr) outerTable.getContent().get(1)).getContent().get(1)).getValue();
        Assert.assertEquals("", new RunAggregator((P) cell.getContent().get(0)).getText()); // empty paragraph, since the last element inside the cell was removed
    }


}
