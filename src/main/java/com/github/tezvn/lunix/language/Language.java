package com.github.tezvn.lunix.language;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * A class represents for a specific language, which contains translated
 * messages.<br>
 * To use languague, it need configuring in {@code config.yml} file with correct
 * locate code (can be seen in {@link Locale #}).
 *
 * @see LanguageManager
 */
public class Language {

    /**
     * The plugin that owns this language.
     */
    private final Plugin plugin;

    /**
     * The locale of the language.
     */
    @Getter
    private final String locale;

    /**
     * A {@link TreeMap} that will store all {@code K-V} pair, which keys are
     * sections and getAll are translated strings in this language.
     */
    private final TreeMap<String, String> translatedMessages = new TreeMap<>();

    private final TreeMap<String, List<String>> translatedList = new TreeMap<>();

    public Language(Plugin plugin, String locale) {
        super();
        this.plugin = plugin;
        this.locale = locale;
    }

    /**
     * Set the {@code section} with {@code value}, which is an already translated
     * string in this language.
     *
     * @param section The section
     * @param value   The translated string
     */
    public void put(String section, String value) {
        translatedMessages.put(section, value);
    }

    public void putList(String section, List<String> list) {
        this.translatedList.put(section, list);
    }

    /**
     * Return a specific translated message in {@code section}.
     *
     * @param section      The section used to retrieve translated message.
     * @param defaultValue The default string if the {@code section} is not available.
     * @return The translated message.
     */
    public String getString(String section, String defaultValue) {
        return translatedMessages.getOrDefault(section, defaultValue);
    }

    public List<String> getList(String section) {
        return this.translatedList.getOrDefault(section, Lists.newArrayList());
    }

    public Plugin getOwner() {
        return plugin;
    }

    @Override
    public String toString() {
        return "Language[plugin=" + getOwner().getName() + ";locale=" + getLocale() + ";translated-messages=" + this.translatedMessages + "]";
    }

    public static class Context {

        private String value;



    }

}

