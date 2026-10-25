# hTimer

English | [Türkçe](README_TR.md)

## Project Overview

hTimer (plugin name: `hSayac`) is a lightweight Paper Minecraft plugin for scheduled countdowns, recurring weekly timers, and automated console commands. Timers are configured in YAML and can be displayed through PlaceholderAPI.

## Features

- One-time fixed-date timers (`FIXED`)
- Weekly recurring timers (`WEEKLY`)
- Console commands executed when a timer reaches its target
- `LONG` and `SHORT` remaining-time formats
- PlaceholderAPI integration
- Configuration reload without restarting the server
- `/htimer` command alias

## Requirements

- Java 17 or newer
- Paper 1.20.4 or a compatible Paper version
- PlaceholderAPI 2.11.7 or newer

PlaceholderAPI is declared as a required dependency in `plugin.yml`.

## Installation

1. Run `mvn clean package` or download a built JAR.
2. Copy `target/hSayac.jar` to the server's `plugins/` directory.
3. Ensure PlaceholderAPI is installed.
4. Start the server once and edit `plugins/hSayac/config.yml`.
5. Apply changes with `/hsayac reload`.

## Commands & Permissions

| Command | Description | Permission |
| --- | --- | --- |
| `/hsayac reload` | Reloads configuration and recalculates timers | `hsayac.admin` |
| `/htimer reload` | Alias for the hSayac command | `hsayac.admin` |

## Configuration

Timers are defined below `counters` in `config.yml`:

```yaml
counters:
  season_end:
    type: "FIXED"
    date: "2026-12-31 23:59"
    format: "LONG"
    end-message: "&cThe timer has ended."
    commands:
      - "broadcast &aThe timer has ended!"

  weekly_event:
    type: "WEEKLY"
    day: "SUNDAY"
    time: "20:00"
    format: "SHORT"
    end-message: "&bThe weekly event has started!"
    commands:
      - "broadcast &aThe event has started!"
```

`FIXED` uses `date` in `YYYY-MM-dd HH:mm` format. `WEEKLY` uses an English day name from `MONDAY` through `SUNDAY` and a 24-hour `HH:mm` time. Commands are dispatched by the console and must not include a leading `/`.

## PlaceholderAPI Integration

Both `hsayac` and `htimer` identifiers are registered:

```text
%hsayac_kalan_<counter_name>%
%htimer_kalan_<counter_name>%
```

Unknown counter names return an empty value. `LONG` produces full Turkish duration text; `SHORT` uses compact labels.

## Building from Source

```bash
mvn -B verify
```

The project targets Java 17, uses Paper API and PlaceholderAPI as provided dependencies, and produces `target/hSayac.jar`. There are currently no test sources, so the build verifies compilation and packaging but does not provide automated behavioral coverage.

## Project Structure

```text
src/main/java/github/heyetcoolio/hSayac/
├── HSayac.java
├── SeasonCommand.java
└── SeasonPlaceholder.java

src/main/resources/
├── plugin.yml
└── config.yml
```

## How It Works

On enable, the plugin loads timers, registers the command and PlaceholderAPI expansions, then checks timer targets every second. Fixed timers are marked finished after execution; weekly timers calculate their next occurrence. Console commands are scheduled on the server thread.

## Known Limitations

- Time calculations use the server JVM's local system time; no separate timezone is configured.
- `end-message` is returned by the placeholder after completion; it is not sent as a standalone player message.
- Invalid timer fields are logged and that timer is skipped, while other timers continue loading.
- There are no automated unit or integration tests yet.
- The periodic task and target Paper behavior should be verified on a staging server.
- Some legacy Bukkit APIs are used and may produce compilation deprecation warnings.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening an issue or pull request. Use Conventional Commits, keep changes focused, and test with `mvn -B verify`.

## License

Copyright (c) 2025 heyetcoolio. This project is licensed under the MIT License; see [LICENSE](LICENSE).

Project repository: [github.com/heyetcoolio/hTimer](https://github.com/heyetcoolio/hTimer)
