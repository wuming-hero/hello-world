# AGENTS.md Design

## Context

This repository is a Java 8 Maven learning and notes project. It contains Java examples, utilities, tests, Markdown technical notes, images, and sample resource files. It is not organized as a production service.

Key project areas:

- `src/main/java/com/wuming`: Java examples and utilities, grouped by topic.
- `src/test/java`: JUnit 4 tests and runnable experiments.
- `document`: Chinese technical notes and study materials.
- `static` and `src/main/resources/static`: images and static assets used by notes or examples.
- `src/main/resources/file`: sample input files for local experiments.

## Goal

Create a root-level `AGENTS.md` that gives future Codex agents concise, project-specific instructions for working in this repository.

The file should be practical rather than exhaustive. It should guide future edits without turning this personal knowledge repository into an overly formal service project.

## Recommended Approach

Use a concise project-instructions format with these sections:

- Project overview
- Repository layout
- Build and test commands
- Code guidelines
- Documentation guidelines
- Safety and maintenance notes

This balances enough context for useful agent behavior with low maintenance cost.

## Content Requirements

`AGENTS.md` should state that:

- The project uses Maven and Java 8.
- General verification is `mvn test`.
- Focused test runs can use `mvn -Dtest=ClassName test`.
- Java changes should remain compatible with Java 8 unless the user explicitly asks otherwise.
- New examples should follow the existing topic-oriented package layout under `com.wuming`.
- Chinese Markdown notes should stay under `document`.
- Images should go under an existing topic directory in `static/image` or `src/main/resources/static/image` when possible.
- Generated build outputs, IDE files, and `target` should not be committed.
- Dependency or JDK upgrades should not be made unless the task explicitly asks for them.

## Non-Goals

The file should not:

- Reorganize the repository.
- Add new build tooling.
- Rewrite README content.
- Document every existing package or note file.
- Introduce stricter engineering process than the repository currently uses.

## Testing and Review

Because the implementation is documentation-only, verification should include:

- Confirm `AGENTS.md` exists at the repository root.
- Review it for clear, actionable instructions.
- Check that commands and paths match the current project structure.

Running Maven is not required for the `AGENTS.md` change itself, but any future Java code change should run the relevant test command.
