package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSortField;
import seedu.address.testutil.PersonBuilder;

class RecentViewTest {
    private final ModelManager model = new ModelManager();

    @Test
    void setPerson_sortedRecentRename_updatesMembershipAndKeepsDescendingSort() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.recordPersonViewed(ALICE);
        model.recordPersonViewed(BENSON);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, true);

        Person renamed = model.setPerson(ALICE, new PersonBuilder(ALICE).withName("Zoe Pauline").build());

        assertEquals(List.of(BENSON.getDisplayName(), renamed.getDisplayName()),
                model.getAddressBook().getRecentContacts());
        assertEquals(List.of(renamed, BENSON), model.getFilteredPersonList());
    }

    @Test
    void recordPersonViewed_sortedRecent_addsNewVisitsWithoutChangingSort() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.addPerson(CARL);
        model.recordPersonViewed(BENSON);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, false);

        model.recordPersonViewed(CARL);
        model.recordPersonViewed(ALICE);
        assertEquals(List.of(ALICE, BENSON, CARL), model.getFilteredPersonList());

        model.recordPersonViewed(BENSON);
        assertEquals(List.of(BENSON.getDisplayName(), ALICE.getDisplayName(), CARL.getDisplayName()),
                model.getAddressBook().getRecentContacts());
        assertEquals(List.of(ALICE, BENSON, CARL), model.getFilteredPersonList());
    }

    @Test
    void recordPersonViewed_sortedRecentAtLimit_replacesOldestContact() {
        List<Person> contacts = new ArrayList<>();
        for (int index = 0; index <= AddressBook.RECENT_CONTACT_LIMIT; index++) {
            contacts.add(model.addPerson(new PersonBuilder().withName("Contact " + (char) ('A' + index))
                    .buildNoIndex()));
        }
        contacts.subList(0, AddressBook.RECENT_CONTACT_LIMIT).forEach(model::recordPersonViewed);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, false);

        model.recordPersonViewed(contacts.getLast());

        assertEquals(contacts.subList(1, contacts.size()), model.getFilteredPersonList());
        assertEquals(AddressBook.RECENT_CONTACT_LIMIT, model.getAddressBook().getRecentContacts().size());
        assertFalse(model.getAddressBook().getRecentContacts().contains(contacts.getFirst().getDisplayName()));
    }

    @Test
    void restoreRecentContacts_sortedRecent_refreshesMembershipAndKeepsSort() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.addPerson(CARL);
        model.recordPersonViewed(BENSON);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, true);

        model.restoreRecentContacts(List.of(ALICE.getDisplayName(), CARL.getDisplayName()));

        assertEquals(List.of(CARL, ALICE), model.getFilteredPersonList());
    }

    @Test
    void setAddressBook_sortedRecent_refreshesMembershipAndKeepsPhoneSort() {
        Person alice = new PersonBuilder(ALICE).withPhone("80000001").build();
        Person benson = new PersonBuilder(BENSON).withPhone("80000002").build();
        Person carl = new PersonBuilder(CARL).withPhone("80000003").build();
        model.addPerson(alice);
        model.recordPersonViewed(alice);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.PHONE, false);
        AddressBook replacement = new AddressBook();
        replacement.addPerson(benson);
        replacement.addPerson(carl);
        replacement.recordPersonViewed(benson);
        replacement.recordPersonViewed(carl);

        model.setAddressBook(replacement);

        assertEquals(List.of(benson, carl), model.getFilteredPersonList());
    }

    @Test
    void showRecentPersons_afterExplicitSort_restoresVisitOrderWithoutChangingPreference() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.recordPersonViewed(ALICE);
        model.recordPersonViewed(BENSON);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, false);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());

        model.showRecentPersons();

        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        assertEquals(PersonSortField.NAME, model.getUserPrefs().getSortField());
        assertFalse(model.getUserPrefs().isSortDescending());
    }

    @Test
    void recordPersonViewed_afterLeavingSortedRecent_doesNotReplaceActiveFilter() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.recordPersonViewed(BENSON);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, false);
        model.updateFilteredPersonList(ALICE::equals);

        model.recordPersonViewed(BENSON);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        model.setFavourite(ALICE, true);
        model.showRecentPersons();
        model.updatePersonListSort(PersonSortField.NAME, false);
        model.showFavouritePersons();
        model.restoreRecentContacts(List.of(BENSON.getDisplayName()));
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }
}
