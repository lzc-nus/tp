---
layout: default.md
title: "Enzo Meng's Project Portfolio Page"
---

### Project: CampusContacts

CampusContacts is a desktop application for university students and staff to manage contacts in their campus community. It is based on AddressBook-Level3 and uses Java and JavaFX. Users interact with it through commands and a graphical interface.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense dashboard](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=enzo-myx&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-09-18T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

* **New feature: Indexed contact identities and name-bucket storage**
    * Designed and implemented stable identifiers for contacts with duplicate names, using display-name suffixes such as `John Tan_2`.
    * Added name-bucket storage, suffix allocation and reuse, and the supporting model, parser, command, and persistence changes.
    * Added legacy-data migration so older flat contact files remain usable and are converted to the current nested format.
    * Substantial implementation work was delivered through [PR #33](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/33) and [PR #47](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/47).

* **New feature: Contact sorting and edit history**
    * Implemented the `sort` command with name and timestamp-based ordering, including ascending and descending order.
    * Added creation and edit timestamps and integrated them with contact editing, display, persistence, and tests.
    * Ensured sorting preferences and contact changes behave consistently across commands and application restarts.
    * Implemented through [PR #57](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/57) and [PR #69](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/69), which also added the associated persistence and regression tests.

* **New feature: Contact details and recently viewed contacts**
    * Implemented the `show NAME[_NUMBER]` workflow for viewing a contact's complete details, including role-specific fields, tags, timestamps, and photos.
    * Implemented recently viewed contact tracking with a ten-contact limit, persistence, duplicate prevention, suffix-aware updates, and the `recent` command.
    * Added rollback handling when saving a recent-contact update fails, so the in-memory history and displayed view remain consistent.
    * The feature and its failure-handling behavior were developed as part of the later alpha-fix work in [PR #114](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/114) and [PR #119](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/119).

* **Enhancements: Reliability and data safety**
    * Added automatic backups when the saved address-book data is corrupted or cannot be read.
    * Improved save-failure handling, including retry behavior and an exit confirmation when unsaved changes remain.
    * Added defensive cleanup for orphaned managed photo files after failed operations or successful saves.
    * Fixed command-result scrolling, `show` window closing behavior, edit-command syntax inconsistencies, and several release-blocking bugs.
    * The main reliability changes were consolidated in [PR #114](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/114) and [PR #119](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/119). I omit small standalone fixes that did not represent a substantial individual contribution.

* **Testing and quality assurance**
    * Added and maintained unit, integration, and workflow tests covering indexed contacts, sorting, edit history, recent contacts, persistence, save failures, photo cleanup, and application startup.
    * Investigated regression and release-candidate issues, fixed Checkstyle failures, and verified behavior through targeted Gradle tests and manual testing.

* **Documentation**
    * Updated the User Guide for indexed contact names, sorting, contact details, recent contacts, data recovery, save behavior, and command syntax.
    * Contributed Developer Guide content describing contact identity and storage, sorting and history, data recovery and save failures, the `show` feature, recently viewed contacts, and manual testing instructions.
    * Added or updated PlantUML diagrams for add-command execution and parsing, save recovery, and recently viewed contacts.
    * The consolidated documentation and help-window updates are recorded in [PR #120](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/120).

* **Additional command and testing work**
    * Updated the `delete` and `edit` command syntax to work with indexed contact names, together with parser, command, and regression tests in [PR #45](https://github.com/AY2627S1-CS2103T-W14-1/tp/pull/45).
    * Maintained the test suite and shared test utilities while implementing the features above; this included coverage for duplicate names, persistence, sorting, edit history, save failures, and recovery behavior.
