package dev.mangojellypudding.nekojs_csv.api;

import com.google.gson.JsonArray;
//import dev.latvian.mods.kubejs.typings.Info;
import dev.mangojellypudding.nekojs_csv.CsvIOImpl;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//@Info("Methods for parsing/reading CSV files to JavaScript objects. Professional!")
@NoArgsConstructor
public class CsvIO {
    public static final String DELIMITER = ",";

//    @Info("""
//            Parse a CSV document from a string. See docs/usage.md in the mod jar for an example""")
    @Nullable
    public List<Map<String, ?>> parse(String string) {
        // Don't use UtilsJS.wrap because it just copies the result of parseReader,
        // wasting performance for no benefits.
        return CsvIOImpl.parseRaw(string);
    }

//    @Info("""
//            Read a CSV document from the file system. See docs/usage.md in the mod jar for an example.
//            Note that KubeJS forbids file system access to files outside the game directory.""")
    @Nullable
    public List<Map<String, ?>> read(Path path) throws IOException {
        return CsvIOImpl.read(path);
    }

//    @Info("""
//            Serialize an array of JavaScript objects to a CSV document string.""")
    public String toString(@Nullable JsonArray content) {
        return CsvIOImpl.toString(content);
    }

//    @Info("""
//            Write an array of JavaScript objects as a CSV document to the file system.
//            Note that KubeJS forbids file system access to files outside the game directory.""")
    public void write(Path path, @Nullable JsonArray content) throws IOException {
        CsvIOImpl.write(path, content);
    }

//    @Info("""
//            Deletes a CSV document from the file system.
//            This is same as `write(path, null)`, added for better readability.""")
    public void delete(Path path) throws IOException {
        CsvIOImpl.write(path, null);
    }
}