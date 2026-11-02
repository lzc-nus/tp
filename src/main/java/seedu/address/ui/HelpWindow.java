package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String HELP_MESSAGE = """
            CAMPUSCONTACTS — COMMAND GUIDE

            Type command names without a leading slash.
            Square-bracketed options are optional.
            Exception: type [blank] literally to remove a photo.

            1. ADD A CONTACT
            Syntax:
              add NAME /role ROLE /email EMAIL /phone PHONE /location LOCATION
              [/major MAJOR] [/year YEAR] [/faculty FACULTY]
              [/department DEPARTMENT] [/hours HOURS] [/photo FILE_PATH]
              [t/TAG]...
            Students may use /major and /year only.
            Professors may use /faculty, /department and /hours only.
            These role-specific fields are optional; fields for the other role are rejected.
            Same-name contacts get the smallest available suffix:
            John Tan, John Tan_2, and so on. Deleted suffixes can be reused.
            Photos are optional. Supported types: PNG, JPG, JPEG, GIF.
            Example:
              add John Tan /role student /email john@u.nus.edu
                  /phone 90000001 /location COM1
                  /photo C:\\Users\\Student\\Pictures\\john.jpg

            2. SHOW CONTACT DETAILS
            Syntax: show NAME[_NUMBER]
            Opens the contact's full details.
            Works even when a filter hides the contact.
            Example: show John Tan_2

            3. EDIT A CONTACT
            Syntax:
              edit NAME[_NUMBER] [/name NAME] [/phone PHONE] [/email EMAIL]
              [/location LOCATION] [/major MAJOR] [/year YEAR]
              [/faculty FACULTY] [/department DEPARTMENT] [/hours HOURS]
              [/photo FILE_PATH or /photo [blank]]
              [t/TAG or /tag TAG]...
            Works even when a filter hides the contact.
            At least one field must be provided. Existing values are overwritten.
            Student-only fields are /major and /year; professor-only fields are
            /faculty, /department and /hours. Fields for the other role are rejected.
            Entering tags replaces all existing tags; use t/ to clear them.
            Use /photo FILE_PATH to add or replace a photo.
            Use /photo [blank] to remove it.
            Renaming assigns the smallest available suffix for the new name.
            Renaming to the same name preserves the contact's current suffix.
            Examples:
              edit Alex Yeoh /phone 91234567
              edit Alex Yeoh /photo C:\\Users\\Student\\Pictures\\new.jpg
              edit Alex Yeoh /photo [blank]

            4. DELETE A CONTACT
            Syntax: delete NAME[_NUMBER]
            Works even when a filter hides the contact.
            Without _NUMBER, targets only the unsuffixed identifier (NAME).
            Example: delete John Tan_2

            5. FIND CONTACTS
            Syntax: find NAME
                    or find /name NAME
            Optional filters: [/role ROLE] [/phone PHONE] [/email EMAIL]
                              [/location LOCATION] [/major MAJOR] [/year YEAR]
                              [/faculty FACULTY] [/department DEPARTMENT]
                              [/hours HOURS] [t/TAG]...
            Searches contact details using case-insensitive substring matching.
            Different fields are combined with AND; multiple values in one field
            are combined with OR. Names may be entered directly or with /name.
            Example: find /name John /location COM1

            6. LIST ALL CONTACTS
            Syntax: list
            Shows every saved contact.

            7. SORT CONTACTS
            Syntax: sort FIELD [asc|desc]
            Fields: name, phone, add, edit. Default order: ascending.
            For add and edit, ascending means most recent first.
            Example: sort name desc

            8. LIST RECENT CONTACTS
            Syntax: recent
            Shows up to ten distinct contacts opened with show,
            most recent first. Recent history is saved across restarts.
            Replaces the current filter and temporarily uses recent-first order.
            list returns to all contacts; sort changes the current displayed order.
            Run recent again to restore recent-first order. Only visible contacts
            can be opened with show; use list first if needed.

            9. FAVOURITE OR UNFAVOURITE A CONTACT
            Syntax: favourite NAME[_NUMBER]
                    unfavourite NAME[_NUMBER]
            Works even when a filter hides the contact.
            Names are case-insensitive. Omitting _NUMBER targets only the
            unsuffixed contact. Repeating a command reports that no change was made.
            Example: favourite John Tan_2

            10. LIST FAVOURITE CONTACTS
            Syntax: favourites
            Shows favourites in the selected sort order and replaces the current filter.
            Use list to return to all contacts. Favourites are saved across restarts;
            deleting or clearing contacts removes the corresponding favourites.

            11. CLEAR ALL CONTACTS
            Syntax: clear
            Type yes to confirm or no to cancel.
            Cancelling leaves contacts, favourites, and recent-contact history unchanged.

            12. OPEN THIS GUIDE
            Syntax: help

            13. EXIT THE APP
            Syntax: exit
            If changes could not be saved, type yes to exit anyway; any other response
            cancels the exit.
            """;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private TextArea helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

}
