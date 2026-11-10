import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final Path DATA_FILE = Paths.get("data", "installed_apps.txt");
    private static final Scanner INPUT = new Scanner(System.in);
    private static final List<App> APPS = List.of(
        new App("com.snapchat.android", "Snapchat", "Social", "12.8.0", 4.2, "85 MB", "Share moments with friends using photos, messages, and stories."),
        new App("com.spotify.music", "Spotify", "Music", "9.1.0", 4.5, "96 MB", "Listen to music, podcasts, and playlists picked for you."),
        new App("com.google.android.youtube", "YouTube", "Video", "20.4.0", 4.4, "72 MB", "Watch videos, explore channels, and follow your interests."),
        new App("com.whatsapp", "WhatsApp", "Communication", "2.25.4", 4.3, "64 MB", "Message and call friends and family around the world."),
        new App("com.google.android.apps.maps", "Google Maps", "Travel", "25.10.0", 4.1, "58 MB", "Find places, get directions, and explore nearby locations."),
        new App("com.duolingo", "Duolingo", "Education", "6.2.0", 4.6, "52 MB", "Learn a new language with short, game-like lessons."),
        new App("com.netflix.mediaclient", "Netflix", "Video", "8.130.0", 4.2, "88 MB", "Stream series, films, and documentaries on your device."),
        new App("com.todoist", "Todoist", "Productivity", "25.3.0", 4.5, "38 MB", "Organize tasks, projects, and everyday reminders."),
        new App("com.mojang.minecraftpe", "Minecraft", "Games", "1.21.0", 4.7, "210 MB", "Build, explore, and create your own block-based worlds."),
        new App("com.google.android.keep", "Google Keep", "Productivity", "5.25.0", 4.3, "24 MB", "Capture quick notes, lists, and reminders in one place.")
    );

    private static class App {
        final String id, name, category, version, size, description;
        final double rating;
        App(String id, String name, String category, String version, double rating, String size, String description) {
            this.id = id; this.name = name; this.category = category;
            this.version = version; this.rating = rating; this.size = size; this.description = description;
        }
    }

    private static Set<String> loadInstalled() {
        try {
            if (Files.exists(DATA_FILE)) {
                Set<String> saved = new LinkedHashSet<>(Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8));
                Set<String> known = new LinkedHashSet<>();
                for (App app : APPS) known.add(app.id);
                saved.retainAll(known);
                return saved;
            }
        } catch (IOException e) {
            System.out.println("  Saved install data could not be read; starting fresh.");
        }
        return new LinkedHashSet<>();
    }

    private static void saveInstalled(Set<String> installed) {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, installed, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("  Could not save install data: " + e.getMessage());
        }
    }

    private static void printApps(List<App> apps, Set<String> installed) {
        if (apps.isEmpty()) { System.out.println("\n  Koi app nahi mili."); return; }
        System.out.println("\n  #  App                  Category           Rating  Size      Status");
        System.out.println("  " + "-".repeat(75));
        for (int i = 0; i < apps.size(); i++) {
            App app = apps.get(i);
            String status = installed.contains(app.id) ? "Installed" : "Available";
            System.out.printf("  %-2d %-20s %-18s %-7.1f %-9s %s%n", i + 1, app.name, app.category, app.rating, app.size, status);
        }
    }

    private static int readNumber(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(INPUT.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Sahi number enter karein."); return -1; }
    }

    private static App chooseApp(List<App> apps, Set<String> installed) {
        if (apps.isEmpty()) { System.out.println("\n  Koi app nahi mili."); return null; }
        printApps(apps, installed);
        int number = readNumber("\n  App number chunein (0 = cancel): ");
        if (number == 0) return null;
        if (number < 1 || number > apps.size()) { System.out.println("  Number list ke andar hona chahiye."); return null; }
        return apps.get(number - 1);
    }

    private static void showDetails(App app, Set<String> installed) {
        String status = installed.contains(app.id) ? "Installed" : "Available to install";
        System.out.println("\n  " + app.name + "  |  " + app.category);
        System.out.println("  " + app.description);
        System.out.printf("  Version: %s   Rating: %.1f/5   Size: %s   Status: %s%n", app.version, app.rating, app.size, status);
        System.out.println("  Package: " + app.id);
    }

    private static void install(Set<String> installed) {
        App app = chooseApp(APPS, installed);
        if (app == null) return;
        if (installed.contains(app.id)) { System.out.println("\n  " + app.name + " pehle se installed hai."); return; }
        System.out.println("\n  " + app.name + " install ho raha hai... (demo)");
        installed.add(app.id); saveInstalled(installed);
        System.out.println("  " + app.name + " successfully installed!");
    }

    private static void uninstall(Set<String> installed) {
        List<App> apps = new ArrayList<>();
        for (App app : APPS) if (installed.contains(app.id)) apps.add(app);
        App app = chooseApp(apps, installed);
        if (app == null) return;
        installed.remove(app.id); saveInstalled(installed);
        System.out.println("\n  " + app.name + " uninstall ho gaya.");
    }

    private static void pause() {
        System.out.print("\n  Menu par jaane ke liye Enter dabayein...");
        INPUT.nextLine();
    }

    private static void browseCategories(Set<String> installed) {
        Set<String> categories = new LinkedHashSet<>();
        for (App app : APPS) categories.add(app.category);
        List<String> list = new ArrayList<>(categories);
        System.out.println("\n  Categories:");
        for (int i = 0; i < list.size(); i++) {
            int count = 0;
            for (App app : APPS) if (app.category.equals(list.get(i))) count++;
            System.out.printf("  %d. %s (%d apps)%n", i + 1, list.get(i), count);
        }
        int number = readNumber("\n  Category number chunein (0 = back): ");
        if (number == 0) return;
        if (number < 1 || number > list.size()) { System.out.println("  Sahi category number enter karein."); return; }
        String category = list.get(number - 1);
        List<App> matches = new ArrayList<>();
        for (App app : APPS) if (app.category.equals(category)) matches.add(app);
        System.out.println("\n  " + category + " apps"); printApps(matches, installed); pause();
    }

    public static void main(String[] args) {
        Set<String> installed = loadInstalled();
        while (true) {
            System.out.println("\n" + "=".repeat(54));
            System.out.println("             MOBILE APP STORE");
            System.out.println("=".repeat(54));
            System.out.println("  1. Browse all apps\n  2. Search apps\n  3. View app details\n  4. Install an app\n  5. Uninstall an app\n  6. My installed apps\n  7. Browse categories\n  0. Exit");
            int choice = readNumber("\n  Apna option chunein: ");
            switch (choice) {
                case 1: printApps(APPS, installed); pause(); break;
                case 2:
                    System.out.print("  App ka naam ya category search karein: ");
                    String query = INPUT.nextLine().trim().toLowerCase();
                    List<App> results = new ArrayList<>();
                    for (App app : APPS) if (!query.isEmpty() && (app.name.toLowerCase().contains(query) || app.category.toLowerCase().contains(query))) results.add(app);
                    printApps(results, installed); pause(); break;
                case 3:
                    App selected = chooseApp(APPS, installed);
                    if (selected != null) { showDetails(selected, installed); pause(); }
                    break;
                case 4: install(installed); break;
                case 5: uninstall(installed); break;
                case 6:
                    List<App> mine = new ArrayList<>();
                    for (App app : APPS) if (installed.contains(app.id)) mine.add(app);
                    printApps(mine, installed); pause(); break;
                case 7: browseCategories(installed); break;
                case 0: System.out.println("\n  App Store use karne ke liye shukriya. Bye!\n"); return;
                default: System.out.println("\n  Invalid option. 0 se 7 ke beech option chunein.");
            }
        }
    }
}
