package jp.bewise.itext7study;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple column renderer example.
 */
public class E01 {

    public static final String DEST = "results/e01/result.pdf";

    public static final String SOURCE_TXT = "src/main/resources/data/日本国憲法.txt";
    public static final String NOTOCJK = "src/main/resources/fonts/NotoSansCJKjp-Medium.otf";

    static PdfFont mainfont = null;

    public static void main(String[] args) throws Exception {
        mainfont = PdfFontFactory.createFont(NOTOCJK, PdfEncodings.IDENTITY_H);
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new E01().createPdf(DEST);
    }

    protected void createPdf(String dest) throws Exception {

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PageSize ps = PageSize.A4;

        // Initialize document
        Document document = new Document(pdf, ps);

        //Set column parameters
        float offSet = 36;
        float columnSpace = 18;
        float columnWidth = (ps.getWidth() - offSet * 2 - columnSpace) / 2;
        float columnHeight = ps.getHeight() - offSet * 2;

        //Define column areas
        Rectangle[] columns = {new Rectangle(offSet, offSet, columnWidth, columnHeight),
                new Rectangle(offSet + columnWidth + columnSpace, offSet, columnWidth, columnHeight)};
        document.setRenderer(new ColumnDocumentRenderer(document, columns));

        String article = new String(Files.readAllBytes(Paths.get(SOURCE_TXT)), StandardCharsets.UTF_8);
        addArticle(document, article);

        document.close();

    }

    public void addArticle(Document doc, String text) throws IOException {
        Paragraph p = new Paragraph("")
                .setFont(mainfont)
                .setFontSize(10)
                .add(text);
        doc.add(p);
    }
}
