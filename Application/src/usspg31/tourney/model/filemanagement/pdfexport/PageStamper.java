package usspg31.tourney.model.filemanagement.pdfexport;

import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Page stamper that adds page numbers to all pages except the first one
 */
public class PageStamper extends PdfPageEventHelper {
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        final int currentPageNumber = writer.getCurrentPageNumber();

        if (currentPageNumber == 1) {
            return;
        }

        final PdfContentByte directContent = writer.getDirectContent();

        directContent.setColorFill(Color.DARK_GRAY);
        directContent.setFontAndSize(
                PDFExporter.TEXT_FONT.getCalculatedBaseFont(false), 10);

        directContent.showTextAligned(Element.ALIGN_CENTER, String
                .valueOf(currentPageNumber),
                writer.getPageSize().getWidth() / 2, writer.getPageSize()
                        .getBottom() * writer.getPageNumber() + 45, 0);
    }
}
