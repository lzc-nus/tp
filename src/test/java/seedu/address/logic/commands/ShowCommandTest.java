package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.ShowCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.testutil.PersonBuilder;

/** Tests for {@link ShowCommand}. */
public class ShowCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingPerson_success() {
        Person personToShow = model.getFilteredPersonList().getFirst();
        ShowCommand showCommand = new ShowCommand(personToShow.getName().fullName, personToShow.getPersonIndex());

        CommandResult expectedResult = new CommandResult(
                String.format(ShowCommand.SHOWING_PERSON_MESSAGE, personToShow.getDisplayName()), personToShow);
        assertCommandSuccess(showCommand, model, expectedResult, model);
    }

    @Test
    public void execute_missingPerson_throwsCommandException() {
        assertCommandFailure(new ShowCommand("Unknown Person"), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
    }

    @Test
    public void execute_duplicateNameWithSuffix_selectsSecondPersonWithoutChangingModel() throws ParseException {
        Model duplicateNameModel = new ModelManager();
        UnindexedPerson firstJohn = new PersonBuilder().withName("John Tan")
                .withPhone("90000001").withEmail("john1@u.nus.edu").buildNoIndex();
        UnindexedPerson secondJohn = new PersonBuilder().withName("John Tan")
                .withPhone("90000002").withEmail("john2@u.nus.edu").buildNoIndex();
        duplicateNameModel.addPerson(firstJohn);
        duplicateNameModel.addPerson(secondJohn);

        Person secondStoredJohn = duplicateNameModel.getFilteredPersonList().stream()
                .filter(person -> person.getDisplayName().equals("John Tan_2"))
                .findFirst()
                .orElseThrow();
        ShowCommand showSecondJohn = new ShowCommandParser().parse("John Tan_2");
        CommandResult expectedResult = new CommandResult(
                String.format(ShowCommand.SHOWING_PERSON_MESSAGE, secondStoredJohn.getDisplayName()), secondStoredJohn);
        Model expectedModel = new ModelManager(duplicateNameModel.getAddressBook(), new UserPrefs());

        assertCommandSuccess(showSecondJohn, duplicateNameModel, expectedResult, expectedModel);
    }

    @Test
    public void execute_filteredPerson_success() {
        model.updateFilteredPersonList(person -> false); // hide all contacts in displayed list
        Person personToShow = model.getAddressBook().getPersonList().getFirst();
        ShowCommand showCommand = new ShowCommand(personToShow.getName().fullName, personToShow.getPersonIndex());
        CommandResult expectedResult = new CommandResult(
                String.format(ShowCommand.SHOWING_PERSON_MESSAGE, personToShow.getDisplayName()), personToShow);
        assertCommandSuccess(showCommand, model, expectedResult, model);
    }

    @Test
    public void equals() {
        ShowCommand firstCommand = new ShowCommand("Alice Pauline");
        ShowCommand secondCommand = new ShowCommand("Benson Meier");

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new ShowCommand("alice pauline")));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
    }
}
