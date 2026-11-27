package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/** Verifies the native help screen and its guide and close actions. */
public class HelpWindowTest extends FxTestSupport {
    @Test
    public void parseCommandFormats_readsAlternativesAndUpdatedSyntax() {
        String guide = String.join("\r\n",
                "# Guide with a `code example` outside a format line",
                "Format: `sort FIELD/DIRECTION [FIELD/DIRECTION]...`, `sort`, or `sort reset`",
                "Format: `clear confirm`",
                "Format: `list [-summary] [s/SUBJECT]... [t/TAG]...`",
                "Format: `archive INDEX`");
        assertEquals(List.of("sort FIELD/DIRECTION [FIELD/DIRECTION]...", "sort", "sort reset",
                "clear confirm", "list [-summary] [s/SUBJECT]... [t/TAG]...", "archive INDEX"),
                HelpWindow.parseCommandFormats(guide));
    }

    @Test
    public void parseCommandFormats_withoutFormats_rejectsEmptyHelp() {
        assertThrows(IllegalStateException.class, () -> HelpWindow.parseCommandFormats("# Guide"));
    }

    @Test
    public void commandFormats_loadFromBundledGuideAndHaveFullTooltips() throws Exception {
        runOnFxThread(() -> {
            HelpWindow window = new HelpWindow(new Stage(), url -> { });
            ScrollPane scroll = (ScrollPane) window.getRoot().getScene().lookup("#commandScroll");
            VBox rows = (VBox) scroll.getContent();
            assertFalse(rows.getChildren().isEmpty());
            assertTrue(rows.getChildren().stream().map(Label.class::cast)
                    .anyMatch(row -> row.getText().startsWith("sort")));
            assertTrue(rows.getChildren().stream().map(Label.class::cast)
                    .anyMatch(row -> row.getText().equals("delete confirm")));
            rows.getChildren().forEach(node -> {
                Label row = (Label) node;
                assertFalse(row.getText().isBlank());
                assertTrue(row.isWrapText());
                assertEquals(row.getText(), row.getTooltip().getText());
            });
        });
    }

    @Test
    public void commandFormats_longCommands_wrapWithoutClippingAtSupportedWidths() throws Exception {
        runOnFxThread(() -> {
            HelpWindow window = new HelpWindow(new Stage(), url -> { });
            window.show();
            Parent root = window.getRoot().getScene().getRoot();
            ScrollPane scroll = (ScrollPane) root.lookup("#commandScroll");
            VBox rows = (VBox) scroll.getContent();
            for (double width : new double[] {726, 1000, 1440}) {
                root.resize(width, 593);
                root.applyCss();
                root.layout();
                for (var node : rows.getChildren()) {
                    Label row = (Label) node;
                    Text rendered = (Text) row.lookup(".text");
                    assertEquals(row.getText(), rendered.getText());
                    assertTrue(row.getWidth() <= scroll.getViewportBounds().getWidth());
                    assertTrue(row.getHeight() >= row.prefHeight(row.getWidth()) - 1);
                    if (row.getText().startsWith("add ") || row.getText().startsWith("edit ")) {
                        assertTrue(row.getHeight() > 47.9);
                    }
                }
                assertTrue(rows.getHeight() > scroll.getViewportBounds().getHeight());
            }
        });
    }

    @Test
    public void guideLink_opensFullGuideInBrowser() throws Exception {
        runOnFxThread(() -> {
            AtomicReference<String> openedUrl = new AtomicReference<>();
            HelpWindow window = new HelpWindow(new Stage(), openedUrl::set);
            Hyperlink guideLink = (Hyperlink) window.getRoot().getScene().lookup("#guideLink");
            guideLink.fire();
            assertEquals(HelpWindow.USERGUIDE_URL, openedUrl.get());
        });
    }

    @Test
    public void focus_minimisedWindow_restoresWindow() throws Exception {
        runOnFxThread(() -> {
            // Not shown, so the iconified state does not depend on the OS window manager
            HelpWindow window = new HelpWindow(new Stage(), url -> { });
            window.getRoot().setIconified(true);
            window.focus();
            assertFalse(window.getRoot().isIconified());
        });
    }

    @Test
    public void closeButton_closesStageAndHelpCanBeOpenedAgain() throws Exception {
        runOnFxThread(() -> {
            HelpWindow window = new HelpWindow(new Stage(), url -> { });
            window.show();
            window.getRoot().getScene().getRoot().applyCss();
            window.getRoot().getScene().getRoot().layout();
            Button close = (Button) window.getRoot().getScene().lookup("#closeButton");
            ImageView icon = (ImageView) close.getGraphic();
            assertFalse(icon.getImage().isError());
            assertEquals(39.32, icon.getFitWidth());
            assertEquals(39.32, icon.getFitHeight());
            assertTrue(close.isCancelButton());
            close.getTooltip().show(close, 0, 0);
            assertEquals("Pixel Code Regular", close.getTooltip().getFont().getFamily());
            assertEquals(18, close.getTooltip().getFont().getSize(), 0.001);
            close.getTooltip().hide();
            ScrollPane scroll = (ScrollPane) window.getRoot().getScene().lookup("#commandScroll");
            Label format = (Label) ((VBox) scroll.getContent()).getChildren().getFirst();
            format.getTooltip().show(format, 0, 0);
            assertEquals("Pixel Code Regular", format.getTooltip().getFont().getFamily());
            assertEquals(18, format.getTooltip().getFont().getSize(), 0.001);
            format.getTooltip().hide();
            assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());
            close.fire();
            assertFalse(window.isShowing());
            window.show();
            assertTrue(window.isShowing());
        });
    }
}
