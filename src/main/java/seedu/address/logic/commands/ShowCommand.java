package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Opens a window containing the full details of one contact. */
public class ShowCommand extends Command {

    public static final String COMMAND_WORD = "show";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the full details of the contact identified by name and optional person index.\n"
            + "Parameters: NAME[_PERSON_INDEX]\n"
            + "Example: " + COMMAND_WORD + " alex yeoh_2";
    public static final String SHOWING_PERSON_MESSAGE = "Opened contact details for: %1$s";

    private final String targetName;
    private final PersonIndex targetIndex;

    /** Creates a command targeting the unsuffixed contact with the given name. */
    public ShowCommand(String targetName) {
        this(targetName, PersonIndex.fromOneBased(1));
    }

    /** Creates a command targeting a contact's name-bucket index. */
    public ShowCommand(String targetName, PersonIndex targetIndex) {
        this.targetName = StringUtil.toProperCase(requireNonNull(targetName));
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> allPersons = model.getAddressBook().getPersonList();
        Person personToShow = allPersons.stream()
                .filter(person -> person.getName().fullName.equals(targetName))
                .filter(person -> targetIndex.equals(person.getPersonIndex()))
                .findFirst()
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME));

        return new CommandResult(String.format(SHOWING_PERSON_MESSAGE, personToShow.getDisplayName()), personToShow);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ShowCommand otherCommand)) {
            return false;
        }
        return targetName.equals(otherCommand.targetName) && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
