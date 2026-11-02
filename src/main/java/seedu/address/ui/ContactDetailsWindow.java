package seedu.address.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.model.person.Person;

/** Controller for a window that displays a contact's complete stored details. */
public class ContactDetailsWindow extends UiPart<Stage> {

    private static final String FXML = "ContactDetailsWindow.fxml";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private Label name;
    @FXML
    private Label contactId;
    @FXML
    private Label role;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label locationLabel;
    @FXML
    private Label majorLabel;
    @FXML
    private Label major;
    @FXML
    private Label yearLabel;
    @FXML
    private Label year;
    @FXML
    private Label facultyLabel;
    @FXML
    private Label faculty;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label department;
    @FXML
    private Label hoursLabel;
    @FXML
    private Label hours;
    @FXML
    private Label tags;
    @FXML
    private Label editHistory;
    @FXML
    private ImageView photo;
    @FXML
    private Label photoStatus;

    public ContactDetailsWindow(Person person, Path dataFilePath) {
        this(new Stage(), person, dataFilePath);
    }

    ContactDetailsWindow(Stage root, Person person, Path dataFilePath) {
        super(FXML, root);
        name.setText(person.getDisplayName());
        contactId.setText(person.getPersonIndex() == null
                ? "Not assigned" : Integer.toString(person.getPersonIndex().getOneBased()));
        role.setText(person.getRole().toString());
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        locationLabel.setText(person.getAddress().value);
        major.setText(person.getMajor().value);
        year.setText(person.getYear().value);
        faculty.setText(person.getFaculty().value);
        department.setText(person.getDepartment().value);
        hours.setText(person.getHours().value);
        setFieldVisibility(person.getMajor().value != null, majorLabel, major);
        setFieldVisibility(person.getYear().value != null, yearLabel, year);
        setFieldVisibility(person.getFaculty().value != null, facultyLabel, faculty);
        setFieldVisibility(person.getDepartment().value != null, departmentLabel, department);
        setFieldVisibility(person.getHours().value != null, hoursLabel, hours);
        tags.setText(person.getTags().isEmpty() ? "None" : person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", ")));
        editHistory.setText(String.format("Added time: %s%nLast Edited: %s",
                formatTimestamp(person.getCreatedAt()), formatTimestamp(person.getLastEditedAt())));
        setPhoto(person, dataFilePath);
        getRoot().addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }

    /** Returns a displayable timestamp, including for records created before edit history was added. */
    private String formatTimestamp(LocalDateTime timestamp) {
        return timestamp == null ? "Unavailable" : timestamp.format(TIMESTAMP_FORMATTER);
    }

    /** Loads the contact photo when it is available inside the application's data directory. */
    private void setPhoto(Person person, Path dataFilePath) {
        if (person.getPhoto().isEmpty()) {
            photoStatus.setText("No photo available");
            return;
        }

        Path dataDirectory = dataFilePath.toAbsolutePath().getParent();
        Path photoPath = dataDirectory.resolve(person.getPhoto().get().getRelativePath()).normalize();
        if (!photoPath.startsWith(dataDirectory) || !Files.isRegularFile(photoPath)) {
            photoStatus.setText("Photo file is unavailable");
            return;
        }

        Image image = new Image(photoPath.toUri().toString());
        if (image.isError()) {
            photoStatus.setText("Photo file cannot be displayed");
            return;
        }

        photo.setImage(image);
        photoStatus.setText("");
    }

    /** Shows and centres the contact-details window. */
    public void show() {
        getRoot().show();
        getRoot().centerOnScreen();
    }

    public boolean isShowing() {
        return getRoot().isShowing();
    }

    public void hide() {
        getRoot().hide();
    }

    /** Hides unused or unspecified fields. */
    private void setFieldVisibility(boolean isSpecified, Label label, Label value) {
        label.setVisible(isSpecified);
        label.setManaged(isSpecified);
        value.setVisible(isSpecified);
        value.setManaged(isSpecified);
    }

    @FXML
    private void handleClose() {
        hide();
    }

    /** Closes the window when the user presses X or Esc. */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.X || event.getCode() == KeyCode.ESCAPE) {
            handleClose();
            event.consume();
        }
    }
}
