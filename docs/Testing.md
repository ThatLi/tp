---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

## GUI regression checks

Run the GUI tests on a machine with a graphical display:

```powershell
.\gradlew.bat test --tests 'seedu.address.ui.*'
```

On macOS or Linux, use `./gradlew` instead of `.\gradlew.bat`. Linux CI uses a
virtual display through `xvfb-run -a`.

| Test class | Checks affected by the interface changes |
| --- | --- |
| `MainWindowTest` | Compact `DD-MMM` and `HH:MM` text, including `31-Dec` / `23:59` changing to `01-Jan` / `00:00`; Pixel Code tooltips on student fields and main-window buttons; system sans-serif fallback for an unavailable font on labels and tooltips. |
| `HelpWindowTest` | Wrapped command formats, scrolling, and Pixel Code tooltips on a command format and the close button. |
| `PersonCardTest` | Student text uses Pixel Code; resizing preserves the 3:2 field ratio and adds a single `…` only when needed. |
| `SubjectGroupPanelTest` | Subject headings and lesson slots use Pixel Code; lesson slots stay at the right edge when rows resize with the application stylesheet applied. |
| `ResultDisplayTest` | Pixel Code feedback wraps, scrolls, and switches between normal and expanded displays as the window resizes. |

`FxTestSupport` loads the bundled Pixel Code and Press Start 2P fonts before GUI
tests run, so standalone component tests use the same fonts as the application
without relying on font installation or another test having opened a window.
Startup smoke tests capture child-process output as UTF-8 so Unicode messages,
including ellipses, can be read consistently on each platform.
Help-format fixtures use `...` for repeated parameters. Prompt and truncation
assertions use the single Unicode character `…`, matching the displayed UI.
See the Developer Guide's [help checks](DeveloperGuide.md#viewing-help) and
[header and command-result checks](DeveloperGuide.md#header-clock-and-command-results)
for manual verification, including display scaling and tooltips.

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
