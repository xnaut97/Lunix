package com.github.tezvn.lunix.language;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.text.DateFormat;
import java.util.*;

/**
 * A class manages all functions relating to {@link Language}.
 *
 * @see Language
 */
public class LanguageManager {


    private static LanguageManager inst;

    /**
     * A {@link HashMap} contains the selected languages of {@link Plugin}s.
     */
    private final Set<Language> availableLanguage = new HashSet<>();

    /**
     * Current selected language.
     */
    private Language currentLanguage;

    /**
     * The default language.
     */
    private Language defaultLanguage;

    /**
     * Instance of owning plugin.
     */
    private final Plugin plugin;

    @ParametersAreNonnullByDefault
    public LanguageManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.saveResource("lang/en-US.yml", false);
        this.registerAvailableLanguages();
        setLanguage(plugin);
    }

    private static boolean isAvailableLocale(String locale) {
        for (Locale loc : DateFormat.getAvailableLocales())
            if (loc.toLanguageTag().equals(locale)) {
                return true;
            }
        return false;
    }

    public static LanguageManager getInst() {
        return inst;
    }

    private void setLanguage(Plugin plugin) {
        String locale = plugin.getConfig().getString("locale", "en-US");
        Optional<Language> newLanguage = getByLocale(locale);
        if (!newLanguage.isPresent()) {
            plugin.getLogger().severe("Could not find language by locale tag '" + locale + "'");
            return;
        }
        setCurrentLanguage(newLanguage.get());
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Return the language file in "plugins/<plugin_name>/lang/" folder.
     */
    public File getFile(@Nonnull Language language) {
        File file = new File("plugins/" + getPlugin().getName() + "/lang/" + language.getLocale() + ".yml");
        if (file.exists() == false)
            throw new NullPointerException("There's not any language file called " + language.getLocale() + ".yml");
        return file;
    }

    /**
     * Register all available language of a plugin in folder
     * "plugins/<plugin_name>/lang/";
     */
    public void registerAvailableLanguages() {
        File folder = new File("plugins/" + getPlugin().getName() + "/lang/");
        if (folder.exists() == false) {
            folder.mkdirs();
        }
        if (!(folder.listFiles() != null && folder.listFiles().length > 0))
            return;
        for (File file : folder.listFiles()) {
            if (file.isDirectory() || !file.getName().endsWith(".yml"))
                continue;

            final String locale = file.getName().replaceAll(".yml", "");
            if (!isAvailableLocale(locale)) {
                Bukkit.getLogger().warning("The language file '" + file.getName()
                        + "' is not a valid language file because its name must be a valid locale tags.");
                continue;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Language language = new Language(getPlugin(), locale);
            boolean defaultLang = false;
            for (String s : config.getKeys(true)) {
                if (s.equals("is-default-language")) {
                    defaultLang = config.getBoolean("is-default-language");
                }
                if (!validate(s, config.getString(s))) continue;

                if (config.isList(s)) {
                    language.putList(s, config.getStringList(s));
                }

                language.put(s, config.getString(s));
            }
            registerLanguage(language);
            if (defaultLang) {
                setDefaultLanguage(language);
                if (getCurrentLanguage() == null)
                    setCurrentLanguage(language);
            }
        }
        Bukkit.getLogger().info("[EmpireX] Registered " + getAvailableLanguage().size() + " languages successfully !");
    }

    /**
     * Return the {@link Language} that has the locale similar to {@code locale}.
     *
     * @return The language with that locale.
     */
    public Optional<Language> getByLocale(String locale) {
        return availableLanguage.parallelStream().filter(l -> l.getLocale().equals(locale)).findAny();
    }

    /**
     * Register a new language.
     *
     * @param language Language that needs registering.
     */
    public void registerLanguage(Language language) {
        availableLanguage.add(language);
    }

    /**
     * Return all available languages.
     */
    public Set<Language> getAvailableLanguage() {
        return availableLanguage;
    }

    /**
     * Return the current selected language of a plugin. If that plugin has not
     * selected any language that, return {@code defaultLanguage}.
     *
     * @param defaultLanguage The default language which will be returned in case the plugin has
     *                        not selected any language yet.
     * @return The current selected language of plugin.
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Change the current language of {@code plugin}.
     *
     * @param language The new language.
     */
    public void setCurrentLanguage(Language language) {
        this.currentLanguage = language;
    }

    private boolean validate(String section, String value) {
		return !value.contains("MemorySection[path='" + section + "', root='YamlConfiguration']");
	}
}
