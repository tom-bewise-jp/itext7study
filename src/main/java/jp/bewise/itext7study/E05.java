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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple column renderer example.
 */
public class E05 {

    public static final String DEST = "results/e05/result.pdf";

    public static final String SOURCE_TXT = "src/main/resources/data/日本国憲法.txt";
    public static final String NOTOSANSCJKMEDIUM = "src/main/resources/fonts/NotoSansCJKjp-Medium.otf";
    public static final String NOTOSANSCJKREGULAR = "src/main/resources/fonts/NotoSansCJKjp-Regular.otf";
    public static final String NOTOSERIFCJKREGULAR = "src/main/resources/fonts/NotoSerifCJKjp-Regular.otf";

    private static PdfFont headlineFont = null;
    private static PdfFont leaderFont = null;
    private static PdfFont bodyFont = null; 

	private static final String gyoumatsuKinsoku = "([｛〔〈《「『【〘〖〝‘“｟«—…‥〳〴〵";
	private static final String gyoutouKinsoku = ",)]｝、〕〉》」』】〙〗〟’”｠»ゝゞーァィゥェォッャュョヮヵヶぁぃぅぇぉっゃゅょゎゕゖㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇷ゚ㇺㇻㇼㇽㇾㇿ々〻‐゠–〜～?!‼⁇⁈⁉・:;/。.";

    public static void main(String[] args) throws Exception {
        headlineFont = PdfFontFactory.createFont(NOTOSANSCJKMEDIUM, PdfEncodings.IDENTITY_H);
        leaderFont = PdfFontFactory.createFont(NOTOSANSCJKREGULAR, PdfEncodings.IDENTITY_H);
        bodyFont = PdfFontFactory.createFont(NOTOSERIFCJKREGULAR, PdfEncodings.IDENTITY_H);
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new E05().createPdf(DEST);
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

        BufferedReader reader = new BufferedReader(new FileReader(SOURCE_TXT));
        String buf;
        Pattern chapter = Pattern.compile("^第[一二三四五六七八九十]+章.*");
        Pattern leader = Pattern.compile("^〔.*〕");
        while ((buf = reader.readLine()) != null) {
        	Matcher mChapter = chapter.matcher(buf);
        	Matcher mLeader = leader.matcher(buf);
        	Text text = new Text(buf);
        	text.setSplitCharacters(
        			(glyphLine, glyphPos)->{
        				if (gyoumatsuKinsoku.indexOf(glyphLine.get(glyphPos).getUnicode()) >= 0)
        					return false;
        				if (glyphPos < glyphLine.size() - 1) {
        					if (gyoutouKinsoku.indexOf(glyphLine.get(glyphPos + 1).getUnicode()) >= 0) {
        						return false;
        					}
        				}
        				return true;
        			});
            Paragraph p = new Paragraph("")
                    .setFont(mChapter.matches()? headlineFont: mLeader.matches()? leaderFont: bodyFont)
                    .setFontSize(mChapter.matches()? 12: 10)
                    .add(text);
            p.setProperty(Property.TEXT_ALIGNMENT, TextAlignment.JUSTIFIED);
            document.add(p);
        }
        reader.close();
        document.close();

    }

}
