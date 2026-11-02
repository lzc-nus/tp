package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

class RecentWorkflowTest {
    @TempDir
    private Path tempDir;

    private final ModelManager model = new ModelManager();

    private Logic newLogic(ModelManager target, JsonAddressBookStorage addressStorage) {
        return new LogicManager(target, new StorageManager(addressStorage,
                new JsonUserPrefsStorage(tempDir.resolve("preferences.json"))));
    }

    private void addJohns(Logic logic) throws Exception {
        logic.execute("add John Tan /role student /email first@u.nus.edu /phone 90000001 /location COM1");
        logic.execute("add John Tan /role student /email second@u.nus.edu /phone 90000002 /location COM2");
    }

    @Test
    void execute_showRepeatAndFailedLookup_tracksOnlySuccessfulSelections() throws Exception {
        Logic logic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("contacts.json")));
        addJohns(logic);
        assertThrows(CommandException.class, () -> logic.execute("show Missing Person"));
        assertTrue(model.getAddressBook().getRecentContacts().isEmpty());
        logic.execute("show John Tan_2");
        logic.execute("show John Tan");
        logic.execute("show John Tan_2");
        logic.execute("list");
        logic.execute("recent");
        assertEquals(List.of("John Tan_2", "John Tan"), model.getAddressBook().getRecentContacts());
        assertEquals("John Tan_2", model.getFilteredPersonList().getFirst().getDisplayName());
        model.updateFilteredPersonList(person -> false);
        logic.execute("show John Tan");
        assertEquals(List.of("John Tan", "John Tan_2"), model.getAddressBook().getRecentContacts());
    }

    @Test
    void execute_elevenShowsAndRestart_preservesOnlyTenMostRecent() throws Exception {
        JsonAddressBookStorage addressStorage = new JsonAddressBookStorage(tempDir.resolve("contacts.json"));
        Logic logic = newLogic(model, addressStorage);
        List<String> visits = new ArrayList<>();
        for (int index = 0; index < 11; index++) {
            String name = "Contact " + (char) ('A' + index);
            logic.execute("add " + name + " /role student /email student" + index
                    + "@u.nus.edu /phone 90000001 /location COM1");
            logic.execute("show " + name);
            visits.addFirst(name);
        }
        ModelManager restarted = new ModelManager(addressStorage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic restartedLogic = newLogic(restarted, addressStorage);
        restartedLogic.execute("recent");
        assertEquals(visits.subList(0, 10), restarted.getAddressBook().getRecentContacts());
        assertEquals(visits.subList(0, 10), restarted.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_deleteSuffixReuseAndClear_doNotLeakHistory() throws Exception {
        JsonAddressBookStorage addressStorage = new JsonAddressBookStorage(tempDir.resolve("contacts.json"));
        Logic logic = newLogic(model, addressStorage);
        addJohns(logic);
        logic.execute("show John Tan_2");
        logic.execute("recent");
        logic.execute("delete John Tan_2");
        assertTrue(model.getFilteredPersonList().isEmpty());
        logic.execute("add John Tan /role student /email third@u.nus.edu /phone 90000003 /location COM3");
        assertTrue(model.getAddressBook().getRecentContacts().isEmpty());
        logic.execute("show John Tan");
        logic.execute("clear");
        logic.execute("no");
        assertEquals(List.of("John Tan"), model.getAddressBook().getRecentContacts());
        logic.execute("clear");
        logic.execute("yes");
        assertTrue(addressStorage.readAddressBook().orElseThrow().getRecentContacts().isEmpty());
    }

    @Test
    void execute_editWhileViewingRecent_keepsRecentViewAndOrder() throws Exception {
        Logic logic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("edit-recent.json")));
        addJohns(logic);
        logic.execute("show John Tan");
        logic.execute("show John Tan_2");
        logic.execute("recent");

        logic.execute("edit John Tan_2 /phone 90000009");

        assertEquals(List.of("John Tan_2", "John Tan"), model.getAddressBook().getRecentContacts());
        assertEquals(List.of("John Tan_2", "John Tan"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
        assertEquals("90000009", model.getFilteredPersonList().getFirst().getPhone().value);
    }

    @Test
    void execute_renameWhileViewingRecent_updatesHistoryAndKeepsRecentView() throws Exception {
        Logic logic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("rename-recent.json")));
        addJohns(logic);
        logic.execute("show John Tan");
        logic.execute("show John Tan_2");
        logic.execute("recent");

        logic.execute("edit John Tan_2 /name Jane Tan");

        assertEquals(List.of("Jane Tan", "John Tan"), model.getAddressBook().getRecentContacts());
        assertEquals(List.of("Jane Tan", "John Tan"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_renameInSortedRecent_keepsNewSuffixVisibleAndPersistsHistory() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("sorted-rename.json"));
        Logic logic = newLogic(model, storage);
        addJohns(logic);
        logic.execute("add Jane Tan /role student /email jane@u.nus.edu /phone 90000003 /location COM3");
        logic.execute("show John Tan");
        logic.execute("show John Tan_2");
        logic.execute("recent");
        logic.execute("sort name desc");

        logic.execute("edit John Tan_2 /name Jane Tan");

        assertEquals(List.of("John Tan", "Jane Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
        assertEquals(List.of("Jane Tan_2", "John Tan"), storage.readAddressBook().orElseThrow().getRecentContacts());
    }

    @Test
    void execute_sortedRecentVisitsAndPhoneEdit_keepSelectedSort() throws Exception {
        Logic logic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("sorted-visits.json")));
        addJohns(logic);
        logic.execute("show John Tan");
        logic.execute("recent");
        logic.execute("sort phone desc");

        logic.execute("show John Tan_2");
        assertEquals(List.of("John Tan_2", "John Tan"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
        logic.execute("show John Tan");
        assertEquals(List.of("John Tan_2", "John Tan"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());

        logic.execute("edit John Tan /phone 90000009");
        assertEquals(List.of("John Tan", "John Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
        logic.execute("delete John Tan");
        logic.execute("show John Tan_2");
        assertEquals(List.of("John Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_recentAfterSorting_restoresVisitOrderAndListUsesSavedSort() throws Exception {
        Logic logic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("sorted-order.json")));
        addJohns(logic);
        logic.execute("show John Tan");
        logic.execute("show John Tan_2");
        logic.execute("recent");
        logic.execute("sort name");
        assertEquals(List.of("John Tan", "John Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());

        logic.execute("recent");
        assertEquals(List.of("John Tan_2", "John Tan"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
        logic.execute("list");
        assertEquals(List.of("John Tan", "John Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_showSaveFailureInSortedRecent_restoresMembershipAndKeepsSort() throws Exception {
        Logic setupLogic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("sorted-save.json")));
        addJohns(setupLogic);
        setupLogic.execute("add Jane Tan /role student /email jane@u.nus.edu /phone 90000003 /location COM3");
        setupLogic.execute("show John Tan");
        setupLogic.execute("show John Tan_2");
        setupLogic.execute("recent");
        setupLogic.execute("sort name");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(tempDir.resolve("failed-sort.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("disk unavailable");
            }
        };

        CommandResult result = newLogic(model, failingStorage).execute("show Jane Tan");

        assertTrue(result.getFeedbackToUser().contains(LogicManager.MESSAGE_SAVE_RETRY_WARNING));
        assertEquals(List.of("John Tan_2", "John Tan"), model.getAddressBook().getRecentContacts());
        assertEquals(List.of("John Tan", "John Tan_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_saveFailure_keepsCommandChangeAndReportsRetryWarning() throws Exception {
        Logic setupLogic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("contacts.json")));
        addJohns(setupLogic);
        setupLogic.execute("show John Tan");
        setupLogic.execute("show John Tan_2");
        setupLogic.execute("recent");
        for (IOException failure : List.of(new IOException("disk unavailable"), new AccessDeniedException("denied"))) {
            JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(tempDir.resolve("failed.json")) {
                @Override
                public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                    throw failure;
                }
            };
            Logic failingLogic = newLogic(model, failingStorage);
            CommandResult result = failingLogic.execute("show John Tan");
            assertTrue(result.getFeedbackToUser().contains(LogicManager.MESSAGE_SAVE_RETRY_WARNING));
            assertEquals(List.of("John Tan_2", "John Tan"), model.getAddressBook().getRecentContacts());
            assertEquals("John Tan_2", model.getFilteredPersonList().get(0).getDisplayName());
        }
    }

    @Test
    void execute_showSaveFailsThenRetrySucceeds_doesNotPersistFailedRecentVisit() throws Exception {
        Logic setupLogic = newLogic(model, new JsonAddressBookStorage(tempDir.resolve("retry-setup.json")));
        addJohns(setupLogic);
        setupLogic.execute("show John Tan_2");
        List<String> originalRecentContacts = List.of("John Tan_2");

        JsonAddressBookStorage retryStorage = new JsonAddressBookStorage(tempDir.resolve("retry.json")) {
            private boolean failNextSave = true;

            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                if (failNextSave) {
                    failNextSave = false;
                    throw new IOException("disk unavailable");
                }
                super.saveAddressBook(addressBook);
            }
        };
        Logic retryingLogic = newLogic(model, retryStorage);

        CommandResult failedResult = retryingLogic.execute("show John Tan");
        assertTrue(failedResult.getFeedbackToUser().contains(LogicManager.MESSAGE_SAVE_RETRY_WARNING));
        assertEquals(originalRecentContacts, model.getAddressBook().getRecentContacts());

        CommandResult retryResult = retryingLogic.execute("list");

        assertTrue(retryResult.getFeedbackToUser().contains(LogicManager.MESSAGE_SAVE_RECOVERED));
        assertEquals(originalRecentContacts, retryStorage.readAddressBook().orElseThrow().getRecentContacts());
    }
}
