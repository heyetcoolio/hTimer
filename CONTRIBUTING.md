# Contributing

Thank you for contributing to hTimer. Keep pull requests focused, documented, and compatible with the supported Paper and Java versions.

## Local setup

Install Java 17 and Maven, clone the repository, and verify the project with:

```bash
mvn -B verify
```

Use a Paper 1.20.4 test server with PlaceholderAPI for behavioral checks.

## Branches and pull requests

Create a focused branch from `main`, for example `feat/weekly-notifications` or `fix/duplicate-execution`. Open a pull request with a clear description, testing notes, and any related issue reference. Do not include credentials, server data, or generated build output.

## Commit messages

Use Conventional Commits:

- `feat: add configurable timer notifications`
- `fix: prevent duplicate timer execution`
- `docs: update installation instructions`
- `refactor: simplify timer scheduling logic`
- `build: configure Maven workflow`

## Coding standards

Follow the existing Java style, keep changes minimal, validate configuration input, preserve thread safety around Bukkit APIs, and update documentation when behavior changes.

## Bug reports

Use the bug report template. Include Minecraft and server software versions, plugin version, reproduction steps, expected and actual behavior, and relevant logs. Never include tokens, passwords, or private server data.
