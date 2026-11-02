package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSortField;
import seedu.address.model.person.UnindexedPerson;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private boolean isRecentView;
    private boolean isRecentOrder;
    private boolean isFavouritesView;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons);
        applySortPreference();
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        if (isRecentView) {
            refreshRecentPersons();
        } else if (isFavouritesView) {
            showFavouritePersons();
        }
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(UnindexedPerson person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public Person addPerson(UnindexedPerson person) {
        Person addedPerson = addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return addedPerson;
    }

    @Override
    public Person setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        Person replacement = addressBook.setPerson(target, editedPerson);
        if (isRecentView) {
            refreshRecentPersons();
        } else if (isFavouritesView) {
            showFavouritePersons();
        }
        return replacement;
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        isRecentView = false;
        isRecentOrder = false;
        isFavouritesView = false;
        filteredPersons.setPredicate(predicate);
        applySortPreference();
    }

    @Override
    public void recordPersonViewed(Person person) {
        addressBook.recordPersonViewed(person);
        if (isRecentView) {
            refreshRecentPersons();
        }
    }

    @Override
    public void restoreRecentContacts(List<String> identifiers) {
        addressBook.setRecentContacts(identifiers);
        if (isRecentView) {
            refreshRecentPersons();
        }
    }

    @Override
    public void showRecentPersons() {
        isRecentView = true;
        isRecentOrder = true;
        isFavouritesView = false;
        refreshRecentPersons();
    }

    private void refreshRecentPersons() {
        List<String> recent = addressBook.getRecentContacts();
        filteredPersons.setPredicate(person -> recent.contains(person.getDisplayName()));
        if (isRecentOrder) {
            // History is newest-first, so a lower position means a more recently viewed contact.
            sortedPersons.setComparator(Comparator.comparingInt(person -> recent.indexOf(person.getDisplayName())));
        } else {
            applySortPreference();
        }
    }

    @Override
    public void setFavourite(Person person, boolean isFavourite) {
        addressBook.setFavourite(person, isFavourite);
        if (isFavouritesView) {
            showFavouritePersons();
        }
    }

    @Override
    public void showFavouritePersons() {
        updateFilteredPersonList(addressBook::isFavourite);
        isFavouritesView = true;
    }

    @Override
    public void updatePersonListSort(PersonSortField field, boolean descending) {
        requireNonNull(field);
        userPrefs.setSortField(field);
        userPrefs.setSortDescending(descending);
        isRecentOrder = false;
        applySortPreference();
    }

    private void applySortPreference() {
        sortedPersons.setComparator(userPrefs.getSortField().getComparator(userPrefs.isSortDescending()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
