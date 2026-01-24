// Copyright (c) 2025 heyetcoolio

package github.heyetcoolio.hSayac;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class HSayac extends JavaPlugin {

    private static HSayac instance;
    public Map<String, Counter> counters = new HashMap<>();

    // Tarih formatlayıcılar
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Warning: NullPointerException (Komut null gelebilir uyarısı çözümü)
        PluginCommand cmd = getCommand("hsayac");
        if (cmd != null) {
            cmd.setExecutor(new SeasonCommand(this));
        } else {
            getLogger().log(Level.SEVERE, "hsayac komutu plugin.yml dosyasında bulunamadı!");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SeasonPlaceholder(this).register();
        }

        loadCounters();
        startCheckTask();
        getLogger().info("hSayac v2.0 aktif! (Clean Code Modu)");
    }

    public void loadCounters() {
        counters.clear();
        ConfigurationSection section = getConfig().getConfigurationSection("counters");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String type = section.getString(key + ".type", "FIXED").toUpperCase();
            List<String> commands = section.getStringList(key + ".commands");
            String endMsg = section.getString(key + ".end-message");

            try {
                Counter counter = new Counter(key, commands, endMsg, type);

                if ("WEEKLY".equals(type)) {
                    String dayStr = section.getString(key + ".day", "MONDAY").toUpperCase();
                    String timeStr = section.getString(key + ".time", "00:00");

                    counter.setRecurrenceDay(DayOfWeek.valueOf(dayStr));
                    counter.setRecurrenceTime(LocalTime.parse(timeStr, timeFormatter));
                    counter.calculateNextOccurrence();

                } else {
                    String dateStr = section.getString(key + ".date");
                    if (dateStr != null) {
                        counter.setTargetDate(LocalDateTime.parse(dateStr, dateTimeFormatter));
                    }
                }

                counters.put(key, counter);
                getLogger().info("Sayac yuklendi: " + key + " -> " + counter.getTargetDate());

            } catch (Exception e) {
                getLogger().severe("HATA: '" + key + "' sayaci yuklenemedi! Tarih formatini kontrol et.");
            }
        }
    }

    private void startCheckTask() {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();

                for (Counter counter : counters.values()) {
                    if (counter.isFinished()) continue;
                    if (counter.getTargetDate() == null) continue;

                    if (now.isAfter(counter.getTargetDate())) {

                        if (counter.getCommands() != null && !counter.getCommands().isEmpty()) {
                            Bukkit.getScheduler().runTask(instance, () -> {
                                for (String cmdStr : counter.getCommands()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', cmdStr));
                                }
                            });
                        }

                        if ("WEEKLY".equals(counter.getType())) {
                            counter.calculateNextOccurrence();
                        } else {
                            counter.setFinished(true);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    @SuppressWarnings("unused")
    public static HSayac getInstance() { return instance; }

    public static class Counter {
        private final String name;
        private final List<String> commands;
        private final String endMessage;
        private final String type;

        private LocalDateTime targetDate;
        private boolean finished = false;
        private DayOfWeek recurrenceDay;
        private LocalTime recurrenceTime;

        public Counter(String name, List<String> commands, String endMessage, String type) {
            this.name = name;
            this.commands = commands;
            this.endMessage = endMessage;
            this.type = type;
        }

        public void calculateNextOccurrence() {
            if (!"WEEKLY".equals(type)) return;

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextTarget = now.with(TemporalAdjusters.nextOrSame(recurrenceDay))
                    .with(recurrenceTime)
                    .withSecond(0).withNano(0);

            if (nextTarget.isBefore(now) || nextTarget.isEqual(now)) {
                nextTarget = nextTarget.plusWeeks(1);
            }

            this.targetDate = nextTarget;
            this.finished = false;
        }

        public String getRemainingFormatted() {
            if (finished) return endMessage;
            if (targetDate == null) return "---";

            Duration duration = Duration.between(LocalDateTime.now(), targetDate);
            if (duration.isNegative()) return "0 sn";

            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;

            if (days > 0) return String.format("%d gün %d sa", days, hours);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        @SuppressWarnings("unused")
        public String getName() { return name; }
        public LocalDateTime getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDateTime targetDate) { this.targetDate = targetDate; }
        public List<String> getCommands() { return commands; }
        @SuppressWarnings("unused")
        public String getEndMessage() { return endMessage; }
        public String getType() { return type; }
        public boolean isFinished() { return finished; }
        public void setFinished(boolean finished) { this.finished = finished; }
        public void setRecurrenceDay(DayOfWeek day) { this.recurrenceDay = day; }
        public void setRecurrenceTime(LocalTime time) { this.recurrenceTime = time; }
    }
}