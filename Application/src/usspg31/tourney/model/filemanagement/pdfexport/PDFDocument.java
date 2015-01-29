package usspg31.tourney.model.filemanagement.pdfexport;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.PdfOutputConfiguration;
import usspg31.tourney.controller.dialogs.PdfOutputConfiguration.TournamentEntry;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentAdministrator;
import usspg31.tourney.model.TournamentRound;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Represents a PDF document that displays an event
 * 
 * @author Jan Tagscherer
 */
public class PDFDocument {
    Document document;
    Event event;

    private int chapterNumber = 1;

    /**
     * Create a new PDF document
     * 
     * @param event
     *            The event to be output
     */
    public PDFDocument(Event event) {
        this.document = new Document();
        this.event = event;
        this.document.setMargins(70, 70, 60, 100);
    }

    /**
     * Open the document
     */
    public void open() {
        this.document.open();
    }

    /**
     * Close the document
     */
    public void close() {
        this.document.close();
    }

    /**
     * Get the underlying PDF document
     * 
     * @return Current PDF document
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * Add meta data to the document
     */
    public void addMetaData() {
        this.document.addTitle(PreferencesManager.getInstance().localizeString(
                "pdfoutput.header.alternativetitle"));
        this.document.addSubject(this.event.getName());
        this.document.addCreator(PreferencesManager.getInstance()
                .localizeString("pdfoutput.header.creationinfo"));
    }

    /**
     * Add the title page of the report containing some information about the
     * event
     * 
     * @throws DocumentException
     *             When the data can not be added to the document
     */
    public void addTitlePage() throws DocumentException {
        /* Add the title of the report */
        Paragraph header = new Paragraph();

        this.addNewLines(header, 3);
        header.add(new Paragraph(PreferencesManager.getInstance()
                .localizeString("pdfoutput.header.title")
                + " \""
                + this.event.getName() + "\"", PDFExporter.BIG_HEADER_FONT));
        header.setSpacingAfter(4);

        this.document.add(header);

        /* Add the creation date of the report */
        Paragraph creationTime = new Paragraph();

        Date currentDate = new Date();
        creationTime.add(new Paragraph(PreferencesManager.getInstance()
                .localizeString("pdfoutput.header.creationtime.before")
                + " "
                + new SimpleDateFormat("dd.MM.yyyy").format(currentDate)
                + " "
                + PreferencesManager.getInstance().localizeString(
                        "pdfoutput.header.creationtime.after")
                + " "
                + new SimpleDateFormat("HH:mm").format(currentDate),
                PDFExporter.META_FONT));

        this.addNewLines(creationTime, 5);

        this.document.add(creationTime);

        /* Add basic information about this event */
        Paragraph eventMeta = new Paragraph();

        eventMeta.add(new Paragraph(event.getName(),
                PDFExporter.MEDIUM_HEADER_FONT));

        /* Add the start and end dates */
        if (this.event.getStartDate() != null
                && this.event.getEndDate() != null) {
            eventMeta.add(new Paragraph(PreferencesManager.getInstance()
                    .localizeString("pdfoutput.event.date.before")
                    + " "
                    + this.event.getStartDate().format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    + " "
                    + PreferencesManager.getInstance().localizeString(
                            "pdfoutput.event.date.after")
                    + " "
                    + this.event.getEndDate().format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    PDFExporter.TEXT_FONT));
        }

        this.addNewLines(eventMeta, 1);

        this.document.add(eventMeta);

        /* Add the location */
        Paragraph location = new Paragraph();

        location.setIndentationLeft(10);
        location.add(new Paragraph(this.event.getLocation(),
                PDFExporter.TEXT_FONT));

        this.addNewLines(location, 3);

        this.document.add(location);

        if (this.event.getAdministrators().size() == 0) {
            for (int i = 0; i < 3; i++) {
                this.document.add(Chunk.NEWLINE);
            }
            return;
        }

        /* Add the event administrators */
        Paragraph administrators = new Paragraph();

        administrators.add(new Paragraph(PreferencesManager.getInstance()
                .localizeString("pdfoutput.event.administration"),
                PDFExporter.MEDIUM_HEADER_FONT));
        this.addNewLines(administrators, 1);

        this.document.add(administrators);

        for (EventAdministrator eventAdministrator : this.event
                .getAdministrators()) {
            Paragraph administratorParagraph = new Paragraph();
            administratorParagraph.setIndentationLeft(10);

            administratorParagraph.add(new Paragraph(eventAdministrator
                    .getFirstName() + " " + eventAdministrator.getLastName(),
                    PDFExporter.SMALL_HEADER_FONT));
            if (!eventAdministrator.getMailAddress().equals("")) {
                administratorParagraph.add(new Paragraph(eventAdministrator
                        .getMailAddress(), PDFExporter.TEXT_FONT));
            }
            if (!eventAdministrator.getPhoneNumber().equals("")) {
                administratorParagraph.add(new Paragraph(PreferencesManager
                        .getInstance().localizeString(
                                "pdfoutput.event.administration.phonenumber")
                        + ": " + eventAdministrator.getPhoneNumber(),
                        PDFExporter.TEXT_FONT));
            }

            this.addNewLines(administratorParagraph, 1);
            this.document.add(administratorParagraph);
        }

        for (int i = 0; i < 3; i++) {
            this.document.add(Chunk.NEWLINE);
        }
    }

    /**
     * Add a watermark that contains the logo of Tourney
     * 
     * @param writer
     *            Writer that is used to output this document
     */
    public void addWatermark(PdfWriter writer) {
        PdfContentByte canvas = writer.getDirectContent();

        Image logo = null;
        try {
            logo = Image.getInstance(this.getClass().getResource(
                    "/ui/icon/icon-1024.png"));
            float logoWidth = logo.getWidth() * 0.035f;
            float logoHeight = logo.getHeight() * 0.035f;
            canvas.addImage(logo, logoWidth, 0, 0, logoHeight, writer
                    .getPageSize().getWidth() / 2 - logoWidth / 2, writer
                    .getPageSize().getBottom() * writer.getPageNumber() + 80);
        } catch (Exception e) {
            e.printStackTrace();
        }

        canvas.setFontAndSize(
                PDFExporter.WATERMARK_FONT.getCalculatedBaseFont(false),
                PDFExporter.WATERMARK_FONT.getSize());
        canvas.setColorFill(PDFExporter.WATERMARK_FONT.getColor());
        canvas.beginText();
        canvas.showTextAligned(Element.ALIGN_CENTER, PreferencesManager
                .getInstance().localizeString("pdfoutput.header.creationinfo"),
                writer.getPageSize().getWidth() / 2, writer.getPageSize()
                        .getBottom() * writer.getPageNumber() + 65, 0);
        canvas.endText();
    }

    /**
     * Add all registered players to the document
     * 
     * @throws DocumentException
     *             When the data can not be added to the document
     */
    public void addRegisteredPlayers() throws DocumentException {
        /* Add a section for the players */
        Anchor playerAnchor = new Anchor(PreferencesManager.getInstance()
                .localizeString("pdfoutput.players.title"),
                PDFExporter.BIG_HEADER_FONT);
        playerAnchor.setName(PreferencesManager.getInstance().localizeString(
                "pdfoutput.players.title"));
        Chapter playerChapter = new Chapter(new Paragraph(playerAnchor),
                this.chapterNumber);
        playerChapter.add(Chunk.NEWLINE);

        for (Player player : this.event.getRegisteredPlayers()) {
            String[][] playerAttributes = {
                    { "Name",
                            player.getFirstName() + " " + player.getLastName() },
                    { "Nickname", player.getNickName() },
                    { "Mail", player.getMailAddress() },
                    { "Starting number", player.getStartingNumber() } };

            for (String[] attribute : playerAttributes) {
                if (attribute[1].equals("")) {
                    if (attribute[0].equals("Starting number")) {
                        playerChapter.add(new Paragraph(PreferencesManager
                                .getInstance().localizeString(
                                        "pdfoutput.players.nostartingnumber"),
                                PDFExporter.TEXT_FONT));
                    } else if (attribute[0].equals("Name")) {
                        playerChapter.add(new Paragraph(PreferencesManager
                                .getInstance().localizeString(
                                        "pdfoutput.players.noname"),
                                PDFExporter.SMALL_BOLD));
                    }
                } else {
                    if (attribute[0].equals("Name")) {
                        playerChapter.add(new Paragraph(attribute[1],
                                PDFExporter.SMALL_BOLD));
                    } else if (attribute[0].equals("Starting number")) {
                        playerChapter.add(new Paragraph(PreferencesManager
                                .getInstance().localizeString(
                                        "pdfoutput.players.startingnumber")
                                + " " + attribute[1], PDFExporter.TEXT_FONT));
                    } else {
                        playerChapter.add(new Paragraph(attribute[1],
                                PDFExporter.TEXT_FONT));
                    }
                }
            }

            if (player.hasPayed()) {
                playerChapter.add(new Paragraph(PreferencesManager
                        .getInstance().localizeString(
                                "pdfoutput.players.payed.true"),
                        PDFExporter.TEXT_FONT));
            } else {
                playerChapter.add(new Paragraph(PreferencesManager
                        .getInstance().localizeString(
                                "pdfoutput.players.payed.false"),
                        PDFExporter.TEXT_FONT));
            }

            if (player.isDisqualified()) {
                playerChapter.add(new Paragraph(PreferencesManager
                        .getInstance().localizeString(
                                "pdfoutput.players.disqualified.true"),
                        PDFExporter.TEXT_FONT));
            }

            playerChapter.add(Chunk.NEWLINE);
        }

        this.chapterNumber++;
        this.document.add(playerChapter);
    }

    /**
     * Add all tournaments to the document including their score tables and
     * tournament histories
     * 
     * @throws DocumentException
     *             When the data can not be added to the document
     */
    public void addTournaments(PdfOutputConfiguration configuration)
            throws DocumentException {
        tournament_loop: for (int tournamentNumber = 0; tournamentNumber < this.event
                .getTournaments().size(); tournamentNumber++) {
            Tournament tournament = this.event.getTournaments().get(
                    tournamentNumber);

            for (TournamentEntry entry : configuration.getTournamentList()) {
                if (entry.getCorrespondingTournament().getId()
                        .equals(tournament.getId())) {
                    if (!entry.shouldBeExported()) {
                        continue tournament_loop;
                    }
                }
            }

            /* Add a section for the tournament */
            Anchor tournamentAnchor = new Anchor(tournament.getName(),
                    PDFExporter.BIG_HEADER_FONT);
            tournamentAnchor.setName(tournament.getName());
            Chapter tournamentChapter = new Chapter(new Paragraph(
                    tournamentAnchor), this.chapterNumber);
            tournamentChapter.add(Chunk.NEWLINE);

            /* Add a subsection for the tournament administrators */
            Paragraph administratorSuperParagraph = new Paragraph(
                    PreferencesManager.getInstance().localizeString(
                            "pdfoutput.tournament.administration"),
                    PDFExporter.MEDIUM_HEADER_FONT);
            Section administratorChapter = tournamentChapter
                    .addSection(administratorSuperParagraph);

            /* Add the tournament administrators */
            Paragraph administrators = new Paragraph();
            if (tournament.getAdministrators().size() == 0) {
                Paragraph noAdministrators = new Paragraph();
                this.addNewLines(noAdministrators, 1);
                noAdministrators.add(new Phrase(PreferencesManager
                        .getInstance().localizeString(
                                "pdfoutput.tournament.administratorsmissing"),
                        PDFExporter.ITALIC_FONT));
                this.addNewLines(noAdministrators, 2);
                administratorChapter.add(noAdministrators);
            } else {
                administratorChapter.add(Chunk.NEWLINE);
                for (TournamentAdministrator eventAdministrator : tournament
                        .getAdministrators()) {
                    Paragraph administratorParagraph = new Paragraph();
                    administratorParagraph.setIndentationLeft(10);

                    administratorParagraph.add(new Paragraph(eventAdministrator
                            .getFirstName()
                            + " "
                            + eventAdministrator.getLastName(),
                            PDFExporter.SMALL_HEADER_FONT));
                    if (!eventAdministrator.getMailAddress().equals("")) {
                        administratorParagraph.add(new Paragraph(
                                eventAdministrator.getMailAddress(),
                                PDFExporter.TEXT_FONT));
                    }
                    if (!eventAdministrator.getPhoneNumber().equals("")) {
                        administratorParagraph
                                .add(new Paragraph(
                                        PreferencesManager
                                                .getInstance()
                                                .localizeString(
                                                        "pdfoutput.event.administration.phonenumber")
                                                + ": "
                                                + eventAdministrator
                                                        .getPhoneNumber(),
                                        PDFExporter.TEXT_FONT));
                    }

                    administrators.add(administratorParagraph);
                }
            }
            this.addNewLines(administrators, 1);

            administratorChapter.add(administrators);

            /* Add a subsection for the result table */
            Paragraph resultParagraph = new Paragraph(PreferencesManager
                    .getInstance().localizeString(
                            "pdfoutput.tournament.table.chaptertitle"),
                    PDFExporter.MEDIUM_HEADER_FONT);
            Section resultChapter = tournamentChapter
                    .addSection(resultParagraph);

            Paragraph scoreTable = new Paragraph();

            /* Get the list of player scores and sort it */
            ObservableList<PlayerScore> clonedPlayerScores = FXCollections
                    .observableArrayList();
            for (PlayerScore score : tournament.getScoreTable()) {
                clonedPlayerScores.add((PlayerScore) score.clone());
            }
            FXCollections.sort(clonedPlayerScores);

            /* Add the player score table */
            if (clonedPlayerScores.size() == 0) {
                Paragraph noScores = new Paragraph();
                this.addNewLines(noScores, 1);
                noScores.add(new Phrase(PreferencesManager.getInstance()
                        .localizeString("pdfoutput.tournament.table.missing"),
                        PDFExporter.ITALIC_FONT));
                resultChapter.add(noScores);
            } else {
                this.addScoreTable(new ArrayList<PlayerScore>(
                        clonedPlayerScores), scoreTable);
            }
            scoreTable.setSpacingBefore(5);
            scoreTable.setSpacingAfter(30);

            resultChapter.add(scoreTable);

            /* Add a subsection for the tournament history */
            Paragraph historyParagraph = new Paragraph(PreferencesManager
                    .getInstance().localizeString(
                            "pdfoutput.tournament.history.chaptertitle"),
                    PDFExporter.MEDIUM_HEADER_FONT);
            Section historyChapter = tournamentChapter
                    .addSection(historyParagraph);

            Paragraph tournamentHistory = new Paragraph();

            if (tournament.getRounds().size() == 0) {
                Paragraph noHistory = new Paragraph();
                this.addNewLines(noHistory, 1);
                noHistory.add(new Phrase(
                        PreferencesManager.getInstance().localizeString(
                                "pdfoutput.tournament.history.missing"),
                        PDFExporter.ITALIC_FONT));
                historyChapter.add(noHistory);
            } else {
                this.addNewLines(tournamentHistory, 1);
                this.addTournamentHistory(tournament, tournamentHistory);
            }
            tournamentHistory.setSpacingBefore(5);
            tournamentHistory.setSpacingAfter(30);

            historyChapter.add(tournamentHistory);

            this.chapterNumber++;
            this.document.add(tournamentChapter);
        }
    }

    /**
     * Add the history of a single tournament
     * 
     * @param tournament
     *            Tournament to be used
     * @param parentParagraph
     *            Paragraph where the history will be appended
     */
    private void addTournamentHistory(Tournament tournament,
            Paragraph parentParagraph) {
        /*
         * Resort all rounds and game phases so they can be easily iterated over
         * later on
         */
        ArrayList<ArrayList<TournamentRound>> resortedRounds = new ArrayList<ArrayList<TournamentRound>>();
        ArrayList<GamePhase> resortedPhases = new ArrayList<GamePhase>();
        resortedPhases.add(PairingHelper.findPhase(tournament.getRounds()
                .get(0).getRoundNumber(), tournament));

        int gamePhaseIndex = 0;
        ArrayList<TournamentRound> currentRounds = new ArrayList<TournamentRound>();
        for (TournamentRound round : tournament.getRounds()) {
            /* The phase is still the same for this round, add it */
            if (PairingHelper.findPhase(round.getRoundNumber(), tournament) == resortedPhases
                    .get(gamePhaseIndex)) {
                currentRounds.add(round);
            } else {
                /* Add the old phases to the big list */
                resortedRounds.add(currentRounds);
                currentRounds.clear();

                /* Add the new game phase */
                resortedPhases.add(PairingHelper.findPhase(
                        round.getRoundNumber(), tournament));
                gamePhaseIndex++;
            }
        }
        resortedRounds.add(currentRounds);

        /* Add the game phase in the first hierarchy level */
        List phaseList = new List(true, false, 15);
        for (GamePhase gamePhase : resortedPhases) {
            Chunk phaseChunk = new Chunk(
                    PreferencesManager.getInstance().localizeString(
                            "pdfoutput.tournament.history.gamephase")
                            + ": "
                            + gamePhase.getPairingMethod().getName()
                            + ", "
                            + PreferencesManager
                                    .getInstance()
                                    .localizeString(
                                            "pdfoutput.tournament.history.roundduration")
                            + ": "
                            + String.format(
                                    "%d:%02d "
                                            + PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.minutes"),
                                    gamePhase.getRoundDuration().getSeconds() / 60,
                                    gamePhase.getRoundDuration().getSeconds() % 60),
                    PDFExporter.SMALL_BOLD);
            ListItem phaseListItem = new ListItem(phaseChunk);
            phaseListItem.setFont(PDFExporter.SMALL_BOLD);

            /* Add the tournament rounds in the second hierarchy level */
            List roundList = new List(true, false, 15);
            for (TournamentRound tournamentRound : resortedRounds.get(gamePhase
                    .getPhaseNumber())) {
                Chunk roundChunk = new Chunk(PreferencesManager.getInstance()
                        .localizeString("pdfoutput.tournament.history.round"),
                        PDFExporter.SMALL_BOLD);
                ListItem roundListItem = new ListItem(roundChunk);
                roundListItem.setFont(PDFExporter.SMALL_BOLD);

                /* Add the pairings in the third hierarchy level */
                List pairingList = new List(true, false, 15);
                for (Pairing pairing : tournamentRound.getPairings()) {
                    Chunk pairingChunk = new Chunk(PreferencesManager
                            .getInstance().localizeString(
                                    "pdfoutput.tournament.history.pairing"),
                            PDFExporter.SMALL_BOLD);
                    ListItem pairingListItem = new ListItem(pairingChunk);
                    pairingListItem.setFont(PDFExporter.SMALL_BOLD);

                    /*
                     * Add the opponents in the pairing in the fourth hierarchy
                     * level
                     */
                    List opponentList = new List(true, false, 15);
                    for (Player opponent : pairing.getOpponents()) {
                        Paragraph opponentParagraph = new Paragraph();
                        Chunk opponentNameChunk = new Chunk(
                                PreferencesManager
                                        .getInstance()
                                        .localizeString(
                                                "pdfoutput.tournament.history.participant")
                                        + ": ", PDFExporter.SMALL_BOLD);
                        opponentParagraph.add(opponentNameChunk);

                        String[] playerData = {
                                opponent.getFirstName() + " "
                                        + opponent.getLastName(),
                                opponent.getNickName(),
                                opponent.getMailAddress() };

                        for (int i = 0; i < playerData.length; i++) {
                            String data = playerData[i];
                            if (!data.equals("")) {
                                opponentParagraph.add(new Chunk(data,
                                        PDFExporter.TEXT_FONT));

                                if (i < playerData.length - 1) {
                                    opponentParagraph.add(new Chunk(", ",
                                            PDFExporter.TEXT_FONT));
                                }
                            }
                        }

                        ListItem opponentListItem = new ListItem(
                                opponentParagraph);
                        opponentListItem.setFont(PDFExporter.SMALL_BOLD);

                        /* Add the scores of the opponent in the fifth hierarchy */
                        List scoreList = new List(true, false, 15);
                        for (PlayerScore playerScore : pairing.getScoreTable()) {
                            if (playerScore.getPlayer().getId()
                                    .equals(opponent.getId())) {
                                for (int i = 0; i < playerScore.getScore()
                                        .size(); i++) {
                                    Integer score = playerScore.getScore().get(
                                            i);

                                    String[] priorityNames = {
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.primary"),
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.secondary"),
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.tertiary"),
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.quaternary"),
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.quinary") };

                                    String priorityName = String.valueOf(i + 1)
                                            + PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "pdfoutput.tournament.history.rating.suffix");

                                    if (i < 5) {
                                        priorityName = priorityNames[i];
                                    }

                                    Chunk playerScoreChunk = new Chunk(
                                            priorityName + ": " + score,
                                            PDFExporter.SMALL_BOLD);
                                    ListItem playerScoreListItem = new ListItem(
                                            playerScoreChunk);
                                    playerScoreListItem
                                            .setListSymbol(new Chunk(""));

                                    scoreList.add(playerScoreListItem);
                                }
                            }
                        }

                        opponentListItem.add(scoreList);

                        opponentList.add(opponentListItem);
                    }

                    pairingListItem.add(opponentList);

                    pairingList.add(pairingListItem);
                }

                roundListItem.add(pairingList);

                roundList.add(roundListItem);
            }

            phaseListItem.add(roundList);

            phaseList.add(phaseListItem);
        }

        parentParagraph.add(phaseList);
    }

    /**
     * Add a score table
     * 
     * @param scoreList
     *            List of scores to be used
     * @param parentParagraph
     *            Paragraph where the score table will be appended
     */
    private void addScoreTable(ArrayList<PlayerScore> scoreList,
            Paragraph parentParagraph) {
        String[] columnHeaders = {
                PreferencesManager.getInstance().localizeString(
                        "pdfoutput.tournament.table.columns.firstname"),
                PreferencesManager.getInstance().localizeString(
                        "pdfoutput.tournament.table.columns.lastname"),
                PreferencesManager.getInstance().localizeString(
                        "pdfoutput.tournament.table.columns.nickname"),
                PreferencesManager.getInstance().localizeString(
                        "pdfoutput.tournament.table.columns.mailaddress"),
                PreferencesManager.getInstance().localizeString(
                        "pdfoutput.tournament.table.columns.startingnumber") };

        /* Create the outer table that will hold all placements */
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100f);

        for (int scoreNumber = 0; scoreNumber < scoreList.size(); scoreNumber++) {
            PlayerScore score = scoreList.get(scoreNumber);

            PdfPCell numberCell = new PdfPCell(new Phrase(PreferencesManager
                    .getInstance().localizeString(
                            "pdfoutput.tournament.table.position")
                    + " " + (scoreNumber + 1), PDFExporter.SMALL_BOLD));
            numberCell.setIndent(3);
            numberCell.setPaddingTop(4);
            numberCell.setPaddingBottom(7);
            numberCell.setBackgroundColor(new Color(240, 240, 240));
            outerTable.addCell(numberCell);

            /* Add the nested table that will hold player information */
            PdfPTable playerTable = new PdfPTable(2);
            try {
                playerTable.setWidths(new float[] { 0.15f, 0.85f });
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            PdfPCell playerCell = new PdfPCell(new Phrase(PreferencesManager
                    .getInstance().localizeString(
                            "pdfoutput.tournament.table.player"),
                    PDFExporter.SMALLER_BOLD));
            playerCell.setIndent(6);
            playerCell.setPaddingBottom(5);
            playerTable.addCell(playerCell);

            /* Add the nested table with the actual player information */
            PdfPTable playerInformationTable = new PdfPTable(5);

            for (String header : columnHeaders) {
                PdfPCell playerHeaderCell = new PdfPCell(new Phrase(header,
                        PDFExporter.SMALLER_BOLD));
                playerHeaderCell.setPaddingBottom(4);
                playerInformationTable.addCell(playerHeaderCell);
            }

            playerInformationTable.setHeaderRows(1);

            Player player = score.getPlayer();
            String[] playerData = { player.getFirstName(),
                    player.getLastName(), player.getNickName(),
                    player.getMailAddress(), player.getStartingNumber() };

            /* Add the actual player data */
            for (String data : playerData) {
                PdfPCell dataCell = new PdfPCell(new Phrase(data,
                        PDFExporter.SMALL_TEXT_FONT));
                dataCell.setPaddingBottom(4);
                playerInformationTable.addCell(dataCell);
            }

            PdfPCell playerInfoCell = new PdfPCell(playerInformationTable);
            playerInfoCell.setBorder(Rectangle.NO_BORDER);
            playerTable.addCell(playerInfoCell);

            PdfPCell playerTableCell = new PdfPCell(playerTable);
            playerTableCell.setBorder(Rectangle.NO_BORDER);
            outerTable.addCell(playerTableCell);

            /*
             * Add the nested table that will hold the scores the player
             * achieved
             */
            PdfPTable scoreTable = new PdfPTable(2);
            try {
                scoreTable.setWidths(new float[] { 0.15f, 0.85f });
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            /* Add the score title cell */
            PdfPCell scoreCell = new PdfPCell(new Phrase(PreferencesManager
                    .getInstance().localizeString(
                            "pdfoutput.tournament.table.rating"),
                    PDFExporter.SMALLER_BOLD));
            scoreCell.setIndent(6);
            scoreCell.setPaddingBottom(5);
            scoreTable.addCell(scoreCell);

            /* Add the nested table with the actual score information */
            int numberOfPriorities = score.getScore().size();
            PdfPTable playerInformationTable1 = new PdfPTable(
                    numberOfPriorities);

            for (int priority = 0; priority < numberOfPriorities; priority++) {
                String priorityName = (priority + 1)
                        + PreferencesManager
                                .getInstance()
                                .localizeString(
                                        "pdfoutput.tournament.table.columns.rating.suffix");

                switch (priority + 1) {
                case 1:
                    priorityName = PreferencesManager
                            .getInstance()
                            .localizeString(
                                    "pdfoutput.tournament.table.columns.rating.primary");
                    break;
                case 2:
                    priorityName = PreferencesManager
                            .getInstance()
                            .localizeString(
                                    "pdfoutput.tournament.table.columns.rating.secondary");
                    break;
                case 3:
                    priorityName = PreferencesManager
                            .getInstance()
                            .localizeString(
                                    "pdfoutput.tournament.table.columns.rating.tertiary");
                    break;
                case 4:
                    priorityName = PreferencesManager
                            .getInstance()
                            .localizeString(
                                    "pdfoutput.tournament.table.columns.rating.quaternary");
                    break;
                case 5:
                    priorityName = PreferencesManager
                            .getInstance()
                            .localizeString(
                                    "pdfoutput.tournament.table.columns.rating.quinary");
                    break;
                }

                PdfPCell priorityHeaderCell = new PdfPCell(new Phrase(
                        priorityName, PDFExporter.SMALLER_BOLD));
                priorityHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                priorityHeaderCell.setPaddingBottom(4);
                playerInformationTable1.addCell(priorityHeaderCell);
            }

            playerInformationTable1.setHeaderRows(1);

            /* Add the actual score data */
            for (int priority = 0; priority < numberOfPriorities; priority++) {
                PdfPCell dataCell = new PdfPCell(new Phrase(
                        String.valueOf(score.getScore().get(priority)),
                        PDFExporter.SMALL_TEXT_FONT));
                dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dataCell.setPaddingBottom(4);
                playerInformationTable1.addCell(dataCell);
            }

            PdfPCell playerInfoCell1 = new PdfPCell(playerInformationTable1);
            playerInfoCell1.setBorder(Rectangle.NO_BORDER);
            scoreTable.addCell(playerInfoCell1);

            PdfPCell playerTableCell1 = new PdfPCell(scoreTable);
            playerTableCell1.setBorder(Rectangle.NO_BORDER);
            outerTable.addCell(playerTableCell1);
        }

        parentParagraph.add(outerTable);
    }

    /**
     * Create a new page
     */
    public void newPage() {
        this.document.newPage();
    }

    /**
     * Add some empty lines
     * 
     * @param paragraph
     *            Paragraph where the lines will be appended
     * @param number
     *            Number of empty lines to add
     */
    private void addNewLines(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(Chunk.NEWLINE);
        }
    }
}
