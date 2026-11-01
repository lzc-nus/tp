---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CampusContacts Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the [AddressBook-Level3 (AB3)](https://github.com/se-edu/addressbook-level3) project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), and [JUnit 5](https://junit.org/junit5/).
* `lzc-nus` used OpenAI Codex for design, implementation, testing, and documentation assistance

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows the general interaction between components when the user issues a command. In CampusContacts, a contact is addressed by its name and, when required, its displayed suffix (for example, `delete John Tan_2`).

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter`,  `HelpWindow`, and `ContactDetailsWindow`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.
* opens a `ContactDetailsWindow` when a successful `show` command returns a selected contact in its `CommandResult`.

#### Event-driven UI interactions

The UI uses JavaFX's event-driven programming model. UI components register event handlers for user interactions, such as button clicks and command submissions. When an event occurs, JavaFX dispatches it to the corresponding handler.

For command execution, the command box's event handler retrieves the entered command and passes it to the `Logic` component. The resulting `CommandResult` is then used to update the result display or trigger other UI actions, such as opening a contact details window.

The UI also observes changes to the model's contact list. When the model updates its observable list, the corresponding UI components are refreshed automatically.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component. The current delete command identifies a contact by name and optional suffix, for example `execute("delete John Tan_2")`.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for a delete command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.
* `AddCommandParser` recognises `/email`, `/phone`, and `/location`, and uses the tokenizer's preamble as the name. It rejects missing fields and duplicate required prefixes, then delegates value validation to `ParserUtil`. Location maps to the existing `Address` value object. Optional tags retain `t/`; edit parsing retains the existing AB3 prefixes. Storage and duplicate-name suffix allocation remain model/storage responsibilities, not parser responsibilities.
* `ShowCommandParser` and `DeleteCommandParser` accept `NAME[_NUMBER]`. The optional number selects a particular contact from a name bucket; omitting it selects the unsuffixed contact.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="600" />


The `Model` component,

* stores address book data in a `UniquePersonHashMap`. It groups contacts by their base name and stores each group in a `TreeMap<Integer, Person>`.
* [gives each stored contact a `PersonIndex`](#adding-contacts-and-assigning-identifiers). The first contact in a name bucket has index `0` and is displayed without a suffix; subsequent contacts are displayed with a suffix derived from their 1-based index, such as `John Tan_2`.
* keeps an unmodifiable flattened `ObservableList<Person>` ordered by base name and then bucket index. This preserves a straightforward list for the existing UI and filtered views.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W14-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)
* saves contacts as nested name buckets under `personsByName`. Each bucket stores a map from the zero-based `PersonIndex` to the contact data.
* supports legacy flat `persons` JSON files when loading. They are converted into buckets in memory and are saved in the new nested form on the next write.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Contact identity and name-bucket storage

CampusContacts allows several contacts to share the same name. `AddressBook` owns a `UniquePersonHashMap`, whose outer `TreeMap` groups contacts by name. Each group is a `PersonNameBucket` containing:

* a `TreeMap<Integer, Person>` for deterministic ordering by `PersonIndex`;
* a priority queue of released indexes, so the smallest available index is reused after a deletion; and
* the next new index to use when there is no released index.

When an `UnindexedPerson` is added, `UniquePersonHashMap` allocates an index and creates the corresponding indexed `Person`. Removing a contact releases its index. The map then refreshes its flattened observable list so the existing list UI remains in sync.

The storage snapshot is structured as follows:

```json
{
  "personsByName": {
    "John Tan": {
      "persons": {
        "0": { "name": "John Tan", "phone": "90000001" },
        "1": { "name": "John Tan", "phone": "90000002" }
      }
    }
  }
}
```

On startup, `JsonSerializableAddressBook` validates each bucket, recreates the indexed `Person` objects from the nested map keys, and passes the result to `AddressBook#setPersonBuckets`. The name bucket rebuilds its released-index queue and next-index value once. Normal add, delete, and edit operations use the in-memory map; `LogicManager` subsequently saves a snapshot through `Storage`.

### Adding contacts and assigning identifiers

Parsing, command execution, and identifier assignment are separate steps. `AddCommandParser`
validates the input and creates an immutable `UnindexedPerson`; `AddCommand` passes that
value to the model, which is the only component responsible for assigning a `PersonIndex`.

`UniquePersonHashMap` finds the contact's name bucket, allocates the smallest available
zero-based index, and calls `UnindexedPerson#withPersonIndex(...)` to create the indexed
`Person`. It inserts that `Person` into the bucket and refreshes the flattened observable
list used by the UI. The temporary unindexed value is not stored after conversion.

The flow is split into parsing and execution below. Together, the diagrams show the full
add operation while keeping the parser's validation responsibilities distinct from the
model's identity-assignment responsibilities.

<puml src="diagrams/AddParsingSequenceDiagram.puml" alt="Parsing an add command" />
<puml src="diagrams/AddExecutionSequenceDiagram.puml" alt="Executing an add command and assigning its identifier" />

### Roles, edit history, and sorting

Contacts have one of two normalized roles: `student` or `professor`. The model uses the
`Student` and `Professor` subclasses to hold role-specific fields. Student contacts may
have a major and year, while professor contacts may have a faculty, department, and office
hours. The parser rejects fields belonging to the other role, so invalid combinations are
stopped before they reach the model.

Each stored contact also has `createdAt` and `lastEditedAt` timestamps. They are persisted
with the contact and are used by `sort add` and `sort edit`. The supported sort fields are
`name`, `phone`, `add`, and `edit`. For timestamp fields, ascending order means most recent
first. Name sorting uses the `PersonIndex` as a tie-breaker, which keeps duplicate names
deterministic even when the direction is reversed. Legacy contacts without timestamps are
given one migration timestamp when loaded.

### Exclusive access to the data folder

`MainApp` acquires a `DataFileLock` before reading contacts or initializing preferences (which also writes to disk).
The lock uses `FileChannel.tryLock()` on `data/campuscontacts.json.lock` and remains held until shutdown finishes
saving preferences. A second launch using the same folder shows an error and exits without initializing its model
or saving preferences. Locking errors also prevent startup; the application does not continue without protection.

The sidecar file stays on disk after exit. Ownership comes from the operating-system lock, not the file's existence,
so an unexpected process exit does not require deleting a stale file. Deleting or replacing the sidecar while the app
is running would undermine this protection. A small synchronized registry rejects repeat acquisitions in the same
JVM before opening another channel, since closing such a channel can release process-level locks on some systems.
This protects app instances using the same data folder; it does not prevent external editors or older app versions
that do not acquire this lock from changing the JSON file.

### Contact photo storage

A contact may have an optional `Photo` reference. When a photo is supplied to `add` or
`edit`, `LogicManager` validates the source and asks `StorageManager` to copy it into the
`photos` directory beside the data file. The contact stores only the generated relative
path, and `JsonAdaptedPerson` persists that path so the photo can be resolved after a
restart. PNG, JPG, JPEG, and GIF files are supported. `edit ... /photo [blank]` removes a
photo.

Photo files are managed separately from the JSON snapshot. After a successful save,
`StorageManager` removes managed photo files that are no longer referenced by any contact.
If importing a photo or applying the command fails, the newly copied file is removed so a
failed command does not leave an orphaned photo.

<puml src="diagrams/PhotoStorageClassDiagram.puml" alt="Class diagram of contact photo references and photo storage" width="550" />

### Data recovery and save failures

If loading the address-book file fails, the application starts with an empty address book
and first preserves the unreadable file in `data/backups/` using a timestamped `.bak` name.
This gives the user a copy to inspect or restore instead of silently overwriting the
original. A legacy flat `persons` file is loaded with missing roles migrated to `student`,
then rewritten in the nested `personsByName` format on the next successful save.

Normal command execution also treats persistence failures explicitly. The command's
in-memory change remains applied, but the failed save marks the session as having unsaved
changes. The next command retries the save. If the user attempts to exit while changes are
still unsaved, the application asks for `yes` to exit anyway; any other response cancels
the exit. Recent-contact history is restored when a save fails, so a failed `show` does not
persist a history change.

<puml src="diagrams/SaveRecoverySequenceDiagram.puml" alt="Save failure recovery and exit confirmation sequence" />

### Show contact details feature

The `show NAME[_NUMBER]` command lets users view the complete stored details of a specific contact without changing its profile fields. A successful selection updates the recently viewed history and, when the recent list is active, moves that contact to the front of the displayed list.

1. `AddressBookParser` delegates the command text to `ShowCommandParser`.
1. `ShowCommand` looks for the matching name and `PersonIndex` in the filtered contact list.
1. On success, it returns a `CommandResult` containing the selected `Person`.
1. `MainWindow` recognises that result and opens `ContactDetailsWindow`.
1. The popup displays the contact's display name, ID, role, phone number, email, location, tags, and photo where available.
1. If the contact is a `Student`, major and/or year may also be displayed where available.
1. If the contact is a `Professor`, faculty, department and/or office hours may also be displayed where available.

The popup is deliberately a UI concern: the command resolves a model object. `LogicManager`
records a successful selection in the recently viewed history and attempts to save it before
returning the result to the UI. If saving fails, the history change is rolled back and a
warning is shown, but the selected contact is still returned to the UI and its details
window still opens; the save is retried by a later command.

### Recently viewed contacts

`AddressBook` stores up to ten recently viewed contact identifiers, newest first. Each identifier is the
contact's existing name and optional suffix. Reopening a contact moves it to the front. Opening an eleventh
distinct contact removes the oldest entry. Callers receive an immutable copy of the history.

<puml src="diagrams/RecentContactsSequenceDiagram.puml" alt="Recording a profile selection and handling save errors" />

`ShowCommand` finds a contact in the displayed list and returns it in `CommandResult`.
`LogicManager` then records the visit and saves the address book. If saving throws `IOException`, including
a permission error, it restores the previous history and reports the error. The result still
contains the selected contact, so the popup opens even though the history update was not
persisted.
A failed contact lookup leaves history unchanged.

`RecentCommand` asks `ModelManager` to show contacts in history order using the existing `FilteredList`
and `SortedList`. This does not change the sort preference in `UserPrefs`. Reopening a contact refreshes
an active recent view. `list`, add or another filter restores the saved sort order. An explicit `sort`
orders the current results normally; `recent` returns them to history order.

`JsonSerializableAddressBook` writes a `recentContacts` array alongside `personsByName`.
A missing or null array loads as empty history. Loading removes missing contacts and repeated identifiers,
then keeps the first ten entries. Null entries or an invalid array structure cause a data-loading error.
Deleting a contact removes its history entry before the suffix can be reused. Editing a contact preserves
its position in history and keeps the recent view active; if its name changes, the history entry is updated
to the new display name. Replacing or clearing the address book also replaces the history.

An ordered list is enough for this feature: it avoids visit timestamps, tied visit times and changes to profile
constructors. Tests cover the ten-contact limit, repeat visits, duplicate names, filters, sorting, storage,
photos, deletion, clear confirmation and failed saves. Manual checks are listed below.

### Favourite contacts

`SetFavouriteCommand` marks or unmarks a contact using the existing `NameIndexParser` grammar.
It searches the whole address book, so users can mark a contact even when it is hidden by a filter.
`ListFavouritesCommand` shows the selected contacts in the user's chosen sort order.
The status command checks the previous selection before updating it, so its feedback distinguishes
an added or removed favourite from an unchanged selection on repeated commands.

`AddressBook` stores favourites as a set of display identifiers: a name and its optional suffix.
These identifiers are unique because contact names cannot contain underscores. Storing selections
separately lets the feature work without changing `Person`, its subclasses or photo handling.
Callers receive an immutable snapshot of the set.

<puml src="diagrams/FavouritesSequenceDiagram.puml" alt="Marking and listing a favourite contact" />

While the favourites view is active, `ModelManager` refreshes it after a status change or edit.
`list` and add return to the full contact list. `AddressBook#setPerson` keeps a contact's selection
when its identifier changes. Deleting or clearing contacts removes their selections, so a new
contact that reuses a suffix starts unmarked.

`JsonSerializableAddressBook` writes a sorted `favouriteContacts` array alongside `personsByName`.
A missing or null array loads as an empty set. References to missing contacts are ignored;
an invalid array structure or null entry causes a data-loading error.
`LogicManager` saves the selections through the existing address-book save flow.

Tests cover identifier parsing, repeated commands, filtered views, sorting, JSON compatibility,
photo preservation, deletion, suffix reuse and clear confirmation. Manual checks are listed below.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete John Tan_2` command to delete the second John Tan in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete John Tan_2` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a university student or teaching staff member
* has a need to find, identify, or contact people within their university community
* prefers desktop apps over other types of applications
* can type fast and prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: CampusContacts helps university students and teaching staff find and identify people they interact with, while being faster than using a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | new user                                   | see usage instructions       | refer to instructions when I forget how to use the App                 |
| `* * *`  | student                                    | add a teaching staff member  | easily retrieve their details when I need academic help                |
| `* * *`  | teaching staff member                      | add a student                | identify and contact them when necessary                               |
| `* * *`  | user                                       | specify if a contact is a student or staff | distinguish their role in the university               |
| `* * *`  | user                                       | delete a contact by name     | remove entries that are no longer relevant                             |
| `* * *`  | user                                       | view a saved contact's full details | identify the correct person before contacting or deleting them |
| `* * *`  | user                                       | list all contacts            | see the list of people I have saved                                        |
| `* * *`  | user                                       | search for a contact by name | quickly find a specific person                                         |
| `* * *`    | user                                       | edit an existing contact    | keep information accurate when someone's details change            |
| `* *`    | user                                       | record a contact's faculty/department | remember where they belong within the university               |
| `* *`    | user                                       | add notes about a contact    | remember the context in which I know them                              |
| `* *`    | user                                       | filter contacts by student/staff role | narrow down my contact list                                    |
| `* *`    | user                                       | attach a photo to a contact | recognise the person more easily                                    |
| `* *`    | user                                       | view recently accessed contacts | quickly return to profiles I viewed earlier                                    |
| `* *`    | user                                       | sort contacts by different attributes | find contacts in a convenient order                                    |
| `* *`    | user                                       | mark contacts as favourites | access frequently needed contacts quickly                                    |
| `* *`    | user                                       | distinguish contacts with identical names | select the correct person                                    |
| `*`      | user                                       | group contacts by modules/projects | find related people much easier                                  |

### Use cases

(For all use cases below, the **System** is `CampusContacts` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a contact**

**MSS**

1.  User requests to add a contact by providing their name, email, phone number, and location.
2.  System adds the contact and displays a success message.

    Use case ends.

**Extensions**

* 1a. User provides an invalid argument.

  * 1a1. System shows an error message indicating an argument is not in the correct format.

    Use case ends.

* 1b. User adds a contact with a name that already exists.

    * 1b1. System assigns the contact an index in that name bucket and displays a suffix when needed to distinguish it.

      Use case ends.

**Use case: Edit a contact**

**MSS**

1. User requests to edit an existing contact by providing its display name and the fields to change.
2. System validates the supplied fields and updates the contact.
3. System displays a success message.

Use case ends.

**Extensions**

* 1a. The provided name does not match any existing contact.
  
  * 1a1. System displays an error message.
    
    Use case ends.
    
* 2a. One or more supplied fields are invalid.
  
  * 2a1. System displays an error message and does not apply the invalid changes.
    
    Use case ends.

**Use case: Add favourite contacts**

**MSS**

1. User requests to mark an existing contact as a favourite.
2. System adds the contact to the favourite selection and displays a success message.
3. User requests to view favourite contacts.
4. System displays the selected contacts in the user's chosen sort order.

Use case ends.

**Extensions**

* 1a. The contact is already a favourite.
  
  * 1a1. System informs the user that the contact is already a favourite.
    
    Use case ends.
    
* 1b. The contact does not exist.
  
  * 1b1. System displays an error message.
    
    Use case ends.

**Use case: Remove favourite contacts**

**MSS**

1. User requests to unmark an existing contact as a favourite.
2. System removes the contact from the favourite selection and displays a success message.

Use case ends.

**Extensions**

* 1a. The contact is already not a favourite.
  
  * 1a1. System informs the user that the contact is already not a favourite.
    
    Use case ends.
  
* 1b. The contact does not exist.
  
  * 1b1. System displays an error message.
    
    Use case ends.

**Use case: Sort contacts**

**MSS**

1. User requests to sort the displayed contacts using a supported attribute and direction.
2. System displays the contacts in the requested order.

Use case ends.

**Extensions**

* 1a. The user supplies an unsupported sorting attribute or direction.
  
  * 1a1. System displays an error message without changing the current sort order.
    
    Use case ends.

**Use case: Delete a contact**

**MSS**

1.  User requests to delete a specific person by their exact display name, optionally including its suffix.
2.  System deletes the person and displays a success message.

    Use case ends.

**Extensions**

* 1a. The provided name does not match any existing contact.

    * 1a1. System shows an error message indicating no contact exists with that name.

      Use case ends.

**Use case: Show a contact's details**

**MSS**

1. User requests the full details of a contact using its display name, for example `show John Tan_2`.
2. System opens a popup containing the contact's saved details.

   Use case ends.

**Extensions**

* 1a. The provided name and suffix do not identify a contact in the current list.

  * 1a1. System shows an error message and does not open a popup.

    Use case ends.

**Use case: Search for a contact**

**MSS**

1.  User requests to search for a contact using a full or partial name.
2.  System searches through the stored contacts.
3.  System displays a list of all matching contacts.

    Use case ends.

**Extensions**

* 2a. No contacts match the provided search term.

    * 2a1. System displays a message indicating that no matching contacts were found.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 contacts without noticeable sluggishness in performance for typical usage.
3.  Should be able to return and display results within 2 seconds when searching through a contact list of up to 1000 contacts.
4.  Should gracefully handle identical contact names without crashing.
5.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Teaching staff**: Professors, lecturers, teaching assistants, or tutors who interact with students within the university
* **Location**: Represents the primary physical location for a contact, symbolizing a home address or residential hostel for students, and an office or cubicle for teaching staff
* **PersonIndex**: A zero-based identifier unique within contacts that share the same base name. It is used for storage and to derive a displayed suffix.
* **Name bucket**: The group of contacts with the same base name.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Closing CampusContacts and subsequent launches

   1. Close the window.
      
   1. Double-click the JAR file in the same folder.>br?
      Expected: The GUI opens with all previous contacts saved automatically.
   
1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
      Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Showing a contact's details

1. Showing an unsuffixed contact

   1. Prerequisites: Add a contact named `John Tan`.

   1. Test case: `show John Tan`<br>
      Expected: A contact-details popup opens and shows John Tan's saved phone, email, location, and tags.

1. Showing a duplicate-name contact

   1. Prerequisites: Add two contacts named `John Tan` with different phone numbers.

   1. Test case: `show John Tan_2`<br>
      Expected: A popup opens for the second John Tan and displays its matching phone number.

1. Invalid contact identifier

   1. Test case: `show Unknown Person`<br>
      Expected: No popup opens. The result area displays an error message.

### Recently viewed contacts

Use a disposable data folder and add two contacts named John Tan with distinct phone/email values.

1. Run `recent` before viewing a profile. The empty-state message should explain `show`.
1. Run `list`, `show John Tan`, close the popup, `show John Tan_2`, then close the popup and run `recent`. Order: John Tan_2, John Tan.
1. Open John Tan again, close the popup, then run `recent`. Order: John Tan, John Tan_2, with no duplicate.
1. Try `show Missing Person` and `recent extra`. They should report errors without changing history.
1. Open nine additional distinct contacts (using `list` if hidden). `recent` must show only the latest ten distinct contacts; evicted contacts remain available with `list`.
1. Run `sort name desc`, then `recent`: latest-first order should return. Run `list`: your saved name-descending order should return.
1. Exit normally and restart in the same folder. `recent` should preserve membership and order.
1. Delete a viewed contact, add a replacement reusing its suffix, and run `recent`. The replacement must not inherit history.
1. Run `clear`, then `no`: history remains. Repeat with `yes`: contacts and history remain empty after restart.

The automated `RecentWorkflowTest` additionally injects ordinary IO and permission failures during `show` saves
and verifies that neither history nor active recent-view order changes. GUI popup rendering is a separate manual check.

### Favourite contacts

Use a disposable data folder. Add two contacts named John Tan with different phone/email values.

1. Run `favourite John Tan_2`, then `favourites`. Only the second John Tan should appear.
1. Repeat `favourite John Tan_2`. It should report `Already in favourites` and remain a single favourite, not a duplicate.
1. Run `sort name desc`, `list`, then `favourites`. The selected sort is retained.
1. Run `unfavourite John Tan_2`. The active favourite view should immediately become empty.
1. Repeat `unfavourite John Tan_2`. It should report `Not in favourites` and leave the view empty.
1. While the view is empty, run `favourite John Tan`. The unsuffixed contact should appear.
1. Run `favourite John Tan_0`, `favourites extra`, and `favourite Missing Person`. Each should report an error without changing selections.
1. Exit normally and relaunch in the same folder. Run `favourites`; the selection should survive.
1. Delete the favourite, add another contact with the same name, and run `favourites`. The replacement must not inherit the selection.
1. Mark a contact; run `clear`, then `no`. Its selection should remain. Repeat `clear`, then `yes`: contacts and selections should both be empty after restart.

### Editing a person

Use a disposable data folder and add two contacts named John Tan with different phone numbers.

1. Editing a contact's basic details

   1. Test case: `edit John Tan /phone 91234567`<br>
   Expected: The phone number of the unsuffixed John Tan is updated, while John Tan_2 remains unchanged.

2. Editing a duplicate-name contact

   1. Test case: `edit John Tan_2 /phone 98765432`<br>
   Expected: Only the second John Tan is updated.

3. Clearing an optional field

   1. Test case: `edit John Tan /major [blank]`<br>
   Expected: The contact's major is cleared, while other fields remain unchanged.

4. Editing a contact while another contact is displayed

   1. Test case: Run `find Jane`, followed by `edit John Tan /phone 91234567`.<br>
   Expected: John Tan is updated even though the displayed list only contains Jane Lim.

5. Editing a nonexistent contact

   1. Test case: `edit Nonexistent Person /phone 91234567`<br>
   Expected: No contact is modified. The result area displays an error message.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete John Tan`<br>
      Expected: The unsuffixed John Tan contact is deleted. The status message shows the deleted contact's details.

   1. Test case: `delete John Tan_2`<br>
      Expected: The contact displayed as John Tan_2 is deleted, while the unsuffixed John Tan remains.

   1. Test case: `delete Jane Lim`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete John Tan_`, `delete John Tan_0`<br>
      Expected: Similar to previous.

2. Deleting a person while a different search result is displayed
   
   1. Prerequisites: Add contacts named John Tan and Jane Lim.
     
   1. Test case: Run `find Jane`, followed by `delete John Tan`.<br>
      Expected: John Tan is deleted even though the displayed list only contains Jane Lim.

### Sorting

Use a disposable data folder. Add several contacts with different names and phone numbers, including two contacts named John Tan with different phone numbers.

1. Sorting by name

   1. Test case: `sort name asc`<br>
   Expected: Contacts are displayed in ascending alphabetical order of their names.

   2. Test case: `sort name desc`<br>
   Expected: Contacts are displayed in descending alphabetical order of their names.

2. Sorting by phone number

   1. Test case: `sort phone asc`<br>
   Expected: Contacts are displayed in ascending order of their phone numbers.

   2. Test case: `sort phone desc`<br>
   Expected: Contacts are displayed in descending order of their phone numbers.

3. Sorting by creation time

   1. Prerequisites: Add three contacts, one after another, with a short delay between additions.

   2. Test case: `sort add asc`<br>
   Expected: Contacts are displayed from most recently created to oldest.

   3. Test case: `sort add desc`<br>
   Expected: Contacts are displayed from oldest to most recently created.

4. Sorting by last-edited time

   1. Prerequisites: Add three contacts, then edit them one after another, with a short delay between edits.

   2. Test case: `sort edit asc`<br>
   Expected: Contacts are displayed from most recently edited to least recently edited.

   3. Test case: `sort edit desc`<br>
   Expected: Contacts are displayed from least recently edited to most recently edited.

5. Sorting duplicate-name contacts

   1. Prerequisites: Add two contacts named John Tan with different phone numbers.

   2. Test case: `sort name asc`<br>
   Expected: John Tan appears before John Tan_2.

   3. Test case: `sort name desc`<br>
   Expected: The duplicate-name contacts remain in deterministic index order, with John Tan appearing before John Tan_2.

6. Sorting while a filter is active

   1. Prerequisites: Add several contacts with different names.

   2. Test case: Run `find John`, followed by `sort name desc`.<br>
   Expected: Only matching contacts are displayed, in descending alphabetical order.

   3. Test case: Run `list`.<br>
   Expected: All contacts are displayed using the saved sort order.

7. Interaction with recently viewed contacts

   1. Prerequisites: View several contacts using the `show` command.

   2. Test case: Run `sort name desc`, followed by `recent`.<br>
   Expected: Contacts are displayed in recently viewed order, newest first.

   3. Test case: Run `list`.<br>
   Expected: All contacts are displayed using the previously selected name-descending sort order.

8. Invalid sorting commands

   1. Test cases: `sort`, `sort invalid asc`, `sort name invalid`, and `sort name asc extra`.<br>
   Expected: Each command displays an appropriate error message without changing the current sort order.

### Contact photos

Use a disposable data folder. Prepare image files in PNG, JPG, JPEG, and GIF formats.

1. Adding a contact with a photo

   1. Test case: Add a contact with a valid PNG photo using the `/photo` prefix.<br>
   Expected: The contact is added successfully and its photo is displayed.

   2. Test case: Repeat using JPG, JPEG, and GIF files.<br>
   Expected: All four supported image formats are accepted.

2. Editing a contact's photo

   1. Prerequisites: Add a contact without a photo.

   2. Test case: Edit the contact and supply a valid image using `/photo`.<br>
   Expected: The contact's photo is updated and displayed.

   3. Test case: Replace the existing photo with another valid image.<br>
   Expected: The new photo is displayed instead of the previous one.

3. Removing a contact's photo

   1. Prerequisites: Add a contact with a photo.

   2. Test case: `edit John Tan /photo [blank]`<br>
   Expected: The contact's photo is removed.

4. Persistence of contact photos

   1. Prerequisites: Add several contacts with photos in different supported formats.

   2. Test case: Close the application and relaunch it using the same data folder.<br>
   Expected: All contacts retain their respective photos.

5. Invalid photo files

   1. Test case: Attempt to add a contact using an unsupported image format.<br>
   Expected: The command is rejected and the contact is not added.

   2. Test case: Attempt to edit a contact using an unsupported image format.<br>
   Expected: The command is rejected and the existing contact details and photo remain unchanged.

   3. Test case: Attempt to add or edit a contact using a nonexistent image file.<br>
   Expected: The command is rejected without leaving an invalid photo reference.

6. Photo cleanup after replacement or removal

   1. Prerequisites: Add a contact with a photo and note the corresponding file in the `photos` directory.

   2. Test case: Replace the photo with another image and allow the command to save successfully.<br>
   Expected: The new photo is retained, and the old managed photo file is removed if it is no longer referenced by any contact.

   3. Test case: Remove the contact's photo using `/photo [blank]` and allow the command to save successfully.<br>
   Expected: The contact no longer references a photo, and the unreferenced managed photo file is removed.

### Saving data

1. Preventing simultaneous access

   1. Prerequisites: Use a disposable writable folder and the same JAR in two terminal windows.
   1. Launch the first instance, then try to launch the second from the same working directory.<br>
      Expected: The second displays an already-in-use error and does not open the contact list.
   1. Dismiss the error. Add a contact in the first instance, exit normally, and relaunch.<br>
      Expected: Startup succeeds and the contact remains saved. The `.lock` file may remain; do not delete it.
   1. Repeat using a separate working directory.<br>
      Expected: Independent data folders can be open at the same time.

1. Dealing with missing/corrupted data files

   1. Prerequisites: Close the application and open `data/campuscontacts.json` in a text editor.

   1. Test case: Change a nested contact's `name` to a different name from its `personsByName` bucket key, then launch the application.<br>
      Expected: The invalid data is rejected and the application starts with an empty address book.

1. Persisting duplicate-name contacts

   1. Prerequisites: Add two contacts named `John Tan`, close the application, then relaunch it.

   1. Expected: Both contacts remain available with the same display identifiers. `data/campuscontacts.json` stores them inside `personsByName` under `John Tan`.

1. _{ more test cases … }_
