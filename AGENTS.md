# Repository Guidelines

## Project Overview

This repository is a personal Java learning and technical-notes project. It combines Java examples, utility classes, JUnit experiments, Markdown notes, images, and sample resource files. Treat it as a study and knowledge-base repository, not as a production service.

## Repository Layout

- `src/main/java/com/wuming`: Java examples and utilities grouped by topic, such as base language features, threading, algorithms, JSON/XML, files, dates, streams, and utility helpers.
- `src/test/java`: JUnit 4 tests and small executable experiments.
- `src/main/resources`: Log configuration, sample files, static images, and other runtime resources used by examples.
- `document`: Chinese technical notes and study materials.
- `static`: Additional static assets, especially images used by notes.

## Build and Test Commands

- Run all tests with `mvn test`.
- Run one test class with `mvn -Dtest=ClassName test`.
- Use Java 8 compatibility unless the user explicitly asks to change the JDK level.

## Code Guidelines

- Follow the existing topic-oriented package layout under `com.wuming`.
- Keep examples small and focused. Prefer adding a new class in the relevant package over mixing unrelated examples into an existing class.
- Preserve existing dependency versions unless the task explicitly asks for an upgrade.
- Avoid broad refactors or package reorganizations unless they are necessary for the requested change.
- Do not commit generated build output, IDE files, or `target`.

## Documentation Guidelines

- Keep Chinese technical notes under `document`.
- Put note images under an existing topic directory in `static/image` or `src/main/resources/static/image` when possible.
- Preserve existing Markdown style and relative image references when editing notes.
- Do not rewrite or reorganize large note sections unless the user asks for that cleanup.

## Verification

- For Java changes, run the most relevant test command, usually `mvn test` or `mvn -Dtest=ClassName test`.
- For Markdown-only changes, review the edited file and check referenced paths; Maven tests are not required unless code or build files changed.
