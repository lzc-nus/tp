---
layout: default.md
title: "Lo Yong Yang's Project Portfolio Page"
---

### Project: CampusContacts

CampusContacts is a desktop application for university students and staff to manage contacts in their campus community. It is based on AddressBook-Level3 (AB3), and uses Java and JavaFX. Users interact with it through commands and a graphical interface.

Given below are my contributions to the project.

* **New feature: Name-bucket contact storage**
  * Replaced the original unique-person list with a name-bucket structure that allows multiple contacts to share the same name while assigning each contact a stable identifier.
  * Added allocation and reuse of the smallest available suffix. For example, if `John Tan` and `John Tan_3` exist, a new John Tan receives `John Tan_2`.
  * Kept the existing observable person list for the UI while using the buckets as the model's source of identity and storage.

* **Enhancement: JSON persistence for name buckets**
  * Added nested JSON storage for contacts grouped by name and suffix, so identifiers remain associated with the correct contact after restarting the app.
  * Added conversion between JSON buckets and the in-memory model, including validation of suffixes and name-to-bucket consistency.
  * Preserved compatibility with older flat contact data so existing data can still be loaded and migrated.
  * Added tests for bucket loading, saving, invalid data, and duplicate contacts.

* **New feature: Show contact details**
  * Added the `show NAME[_NUMBER]` command to open a popup with a selected contact's full details.
  * Used the optional suffix to distinguish contacts with the same name, while keeping the command read-only.
  * Added a JavaFX details window and command tests for selection and parsing.

* **New feature: Contact photos**
  * Added an optional `/photo FILE_PATH` field when adding a contact.
  * Validated the supplied path and image content, then copied the image into app-managed photo storage. The contact stores a reference to the managed photo rather than depending on the original file location.
  * Displayed a contact's photo in the details popup and added photo cleanup when contacts are removed or an add operation fails.
  * Added regression tests for photo parsing, persistence, copying, deletion, and invalid photo files.

* **Enhancement: In-app command guide**
  * Replaced the external help link with an in-app command guide.
  * Organized the commands into numbered sections and explained optional fields and photo input so users can learn the command format without leaving the application.

* **Bug fixes and verification**
  * Investigated and fixed issues involving same-name contact identifiers, contact editing, photo validation, and storage cleanup.
  * Ran targeted Gradle tests and Checkstyle checks during implementation, and smoke-tested the packaged JAR by launching the application and exercising its commands.

* **Project and release work**
  * As team lead, coordinated feature work and integration with the team's upstream repository, including resolving merge conflicts and checking CI failures.
  * Prepared release artifacts with Gradle's `shadowJar` task, checked that the JAR launched, and published release notes and the downloadable JAR on GitHub.

* **Documentation**
  * Updated user-facing command documentation for contact identifiers, photo attachments, and the available commands.
  * Contributed a PlantUML class diagram for the photo-storage design to the Developer Guide.
