package dev.mangojellypudding.nekojs_csv;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.mangojellypudding.nekojs_csv.api.CsvIO;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
 * Original code by ChloePrime (https://github.com/ChloePrime/KubeJS-CSV)
 * Ported to NekoJS by [MangoJellyPudding]
 * Licensed under MIT.
 */
public class CsvIOImpl {
    static Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setStrictness(Strictness.LENIENT) // 替換舊的 setLenient()
            .serializeNulls()
            .create();

    public static void checkFilePermission(Path path) {
        if (!path.getFileName().toString().endsWith(".csv")) {
            throw new SecurityException("Can't access non-CSV files");
        }
    }

    public static List<Map<String, ?>> parseRaw(@Nullable String content) {
        if (content == null) {
            return null;
        }
        var iterator = content.lines().iterator();
        return parse0(() -> iterator.hasNext() ? iterator.next() : null);
    }

    public static List<Map<String, ?>> parseReader(@Nullable Reader content) throws IOException {
        if (content == null) {
            return Collections.emptyList();
        }
        try (var reader = content instanceof BufferedReader buffered ? buffered : new BufferedReader(content)) {
            return parse0(() -> {
                try {
                    return reader.readLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    @Nullable
    public static List<Map<String, ?>> read(Path path) throws IOException {
        checkFilePermission(path);
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (var reader = Files.newBufferedReader(path)) {
            return CsvIOImpl.parseReader(reader);
        }
    }

    private static List<Map<String, ?>> parse0(Supplier<String> reader) {
        var header = reader.get();
        if (header == null) {
            return Collections.emptyList();
        }
        var keys = header.split(CsvIO.DELIMITER);
        if (keys.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<Map<String, ?>>();
        while (true) {
            var line = reader.get();
            if (line == null) {
                break;
            }
            if (line.isEmpty()) {
                continue;
            }

            var values = line.split(CsvIO.DELIMITER);
            var length = Math.min(keys.length, values.length);
            var parsedLine = new LinkedHashMap<String, Object>();
            for (int i = 0; i < length; i++) {
                parsedLine.put(keys[i], stringToPrimitive(values[i]));
            }
            result.add(parsedLine);
        }
        return result;
    }

    private static Object stringToPrimitive(String string) {
        // Boolean
        if ("true".equalsIgnoreCase(string)) {
            return true;
        }
        if ("false".equalsIgnoreCase(string)) {
            return false;
        }

        // Numbers
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ignored) {
        }

        // String
        return string;
    }

    private static final TypeToken<Map<String, Object>> ENTRY_TYPE = new TypeToken<>() {};

    public static String toString(@Nullable JsonArray content) {
        if (content == null || content.isJsonNull()) {
            return "";
        }
        var sb = new StringBuilder();
        write(sb::append, content);
        return sb.toString();
    }

    public static void write(Path path, @Nullable JsonArray content) throws IOException {
        checkFilePermission(path);

        if (content == null || content.isJsonNull()) {
            Files.deleteIfExists(path);
            return;
        }

        try (var writer = Files.newBufferedWriter(path)) {
            write(s -> {
                try {
                    writer.write(s);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, content);
        }
    }

    private static void write(Consumer<String> writer, @Nullable JsonArray content) {
        if (content == null || content.isJsonNull()) {
            return;
        }

        Map<?, ?>[] entries = content.asList().stream()
                .filter(element -> element instanceof JsonObject)
                .map(element -> (Map<?, ?>) GSON.fromJson(element, ENTRY_TYPE.getType()))
                .toArray(Map<?, ?>[]::new);
        Object[] keys = Arrays.stream(entries)
                .flatMap(map -> map.keySet().stream())
                .distinct()
                .toArray(Object[]::new);

        // Header
        var header = Arrays.stream(keys).map(Object::toString).collect(Collectors.joining(CsvIO.DELIMITER));
        writer.accept(header);
        writer.accept("\n");
        // Values
        for (Map<?, ?> entry : entries) {
            for (int i = 0; i < keys.length; i++) {
                Object key = keys[i];
                Object value = entry.get(key);
                writer.accept(value == null ? "" : value.toString());
                if (i < keys.length - 1) {
                    writer.accept(",");
                }
            }
            writer.accept("\n");
        }
    }
}