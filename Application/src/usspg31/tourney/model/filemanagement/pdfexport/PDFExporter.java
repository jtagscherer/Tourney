package usspg31.tourney.model.filemanagement.pdfexport;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import usspg31.tourney.controller.dialogs.PdfOutputConfiguration;
import usspg31.tourney.model.Event;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Contains a static method that can export an event as a PDF file and some
 * fonts that will be used
 * 
 * @author Jan Tagscherer
 */
public class PDFExporter {
    public static final Font BIG_HEADER_FONT = new Font(Font.HELVETICA, 20,
            Font.BOLD);
    public static final Font MEDIUM_HEADER_FONT = new Font(Font.HELVETICA, 16,
            Font.BOLD);
    public static final Font SMALL_HEADER_FONT = new Font(Font.HELVETICA, 12,
            Font.BOLD);
    public static final Font META_FONT = new Font(Font.HELVETICA, 14,
            Font.BOLD, Color.GRAY);
    public static final Font SMALL_BOLD = new Font(Font.HELVETICA, 12,
            Font.BOLD);
    public static final Font SMALLER_BOLD = new Font(Font.HELVETICA, 10,
            Font.BOLD);
    public static final Font TEXT_FONT = new Font(Font.HELVETICA, 12,
            Font.NORMAL);
    public static final Font SMALL_TEXT_FONT = new Font(Font.HELVETICA, 10,
            Font.NORMAL);
    public static final Font ITALIC_FONT = new Font(Font.HELVETICA, 12,
            Font.ITALIC);
    public static final Font WATERMARK_FONT = new Font(Font.HELVETICA, 10,
            Font.NORMAL, Color.DARK_GRAY);

    /**
     * Export an event as a PDF file
     * 
     * @param event
     *            Event to be exported
     * @param filePath
     *            Path where the PDF file will be created
     * @throws FileNotFoundException
     *             If the file could not be created
     * @throws DocumentException
     *             If the document could not be written
     */
    public static void exportEventAsPdf(Event event, String filePath,
            PdfOutputConfiguration configuration)
            throws FileNotFoundException, DocumentException {
        PDFDocument document = new PDFDocument(event);
        PdfWriter writer = PdfWriter.getInstance(document.getDocument(),
                new FileOutputStream(filePath));
        writer.setPageEvent(new PageStamper());
        document.open();

        document.addMetaData();
        document.addTitlePage();
        document.addWatermark(writer);
        document.newPage();
        if (configuration.exportPlayerList()) {
            document.addRegisteredPlayers();
        }
        if (configuration.exportTournaments()) {
            document.addTournaments(configuration);
        }
        document.close();
    }

}
