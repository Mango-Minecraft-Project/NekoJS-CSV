package dev.mangojellypudding.nekojs_csv.js.type_adapter;

import com.tkisor.nekojs.api.JSTypeAdapter;
import org.graalvm.polyglot.Value;

import javax.annotation.Nullable;
import java.nio.file.Path;

public final class PathAdapter implements JSTypeAdapter<Path> {

    @Override
    public Class<Path> getTargetClass() {
        return Path.class;
    }

    @Override
    public boolean canConvert(Value value) {
        if (value.isNull()) {
            return true;
        }
        if (value.isString()) {
            return true;
        }
        if (value.isHostObject()) {
            return value.asHostObject() instanceof Path;
        }
        return false;
    }

    @Override
    public Path convert(Value value) {
        if (value.isNull()) {
            return Path.of(".");
        }

        if (value.isString()) {
            return stringToPath(value.asString());
        }

        if (value.isHostObject()) {
            Object obj = value.asHostObject();

            if (obj instanceof Path path) {
                return path;
            }
        }

        return Path.of(".");
    }

    /**
     * 将字符串转换为 Path。
     */
    @Nullable
    static Path stringToPath(String str) {
        if (str == null || str.trim().isEmpty()) return null;

        try {
            // 1. 獲取當前工作目錄的絕對路徑 (.minecraft)
            Path baseDir = Path.of(".").toAbsolutePath().normalize();

            // 2. 將輸入轉換為絕對路徑並標準化（處理掉 ../ 等危險符號）
            Path targetPath = Path.of(str).toAbsolutePath().normalize();

            // 3. 檢查目標路徑是否以 baseDir 開頭
            if (!targetPath.startsWith(baseDir)) {
                // 如果嘗試跳出目錄，可以選擇返回 null 或拋出異常
                return null;
            }

            return targetPath;
        } catch (Exception e) {
            return null;
        }
    }
}
