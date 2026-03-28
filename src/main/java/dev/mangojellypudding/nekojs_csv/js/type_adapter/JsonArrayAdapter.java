package dev.mangojellypudding.nekojs_csv.js.type_adapter;

import com.google.gson.*;
import com.tkisor.nekojs.api.JSTypeAdapter;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;

public final class JsonArrayAdapter implements JSTypeAdapter<JsonArray> {

    @Override
    public Class<JsonArray> getTargetClass() {
        return JsonArray.class;
    }

    @Override
    public boolean canConvert(Value value) {
        if (value.isNull()) {
            return true;
        }
        if (value.isIterator() || value.hasArrayElements()) {
            return true;
        }
        if (value.isHostObject()) {
            return value.asHostObject() instanceof Path;
        }
        return false;
    }

    @Override
    public JsonArray convert(Value value) {
        // 1. 基本 Null 檢查
        if (value == null || value.isNull()) {
            return new JsonArray();
        }

        // 2. 處理 Host Object (Java 對象)
        if (value.isHostObject()) {
            Object obj = value.asHostObject();
            // 如果已經是 JsonArray，直接回傳
            if (obj instanceof JsonArray jsonArray) {
                return jsonArray;
            }
            // 如果是其他的 Java 集合，先轉成 JSON 字串再由 Gson 解析（最穩定的做法）
            // 或者手動遍歷。這裡示範手動遍歷以維持效能：
            if (obj instanceof Iterable<?> iterable) {
                JsonArray array = new JsonArray();
                for (Object item : iterable) {
                    array.add(serializeToElement(item));
                }
                return array;
            }
        }

        // 3. 處理 Polyglot 陣列 (例如 JS 的 [1, 2, 3])
        if (value.hasArrayElements()) {
            JsonArray jsonArray = new JsonArray();
            for (long i = 0; i < value.getArraySize(); i++) {
                // 注意：Gson 的 add 需要 JsonElement
                jsonArray.add(serializeToElement(value.getArrayElement(i)));
            }
            return jsonArray;
        }

        // 4. 處理 Iterator
        if (value.isIterator()) {
            JsonArray jsonArray = new JsonArray();
            while (value.hasIteratorNextElement()) {
                jsonArray.add(serializeToElement(value.getIteratorNextElement()));
            }
            return jsonArray;
        }

        return new JsonArray();
    }

    /**
     * 輔助方法：將 Value 或 Object 轉為 Gson 的 JsonElement
     */
    private JsonElement serializeToElement(Object target) {
        if (target instanceof Value v) {
            // 1. 處理基礎型別 (Number, Boolean, String)
            if (v.isNumber()) {
                // 使用 as(Object.class) 會自動轉為 Integer, Long, Double 等
                return new JsonPrimitive((Number) v.as(Object.class));
            }
            if (v.isBoolean()) {
                return new JsonPrimitive(v.asBoolean());
            }
            if (v.isString()) {
                return new JsonPrimitive(v.asString());
            }

            // 2. 處理 Null
            if (v.isNull()) {
                return JsonNull.INSTANCE;
            }

            // 3. 處理複雜物件 (例如 JS Object 轉成 Gson 的 JsonObject)
            // 這裡建議直接用 Gson 序列化其 Java 形式
            return new Gson().toJsonTree(v.as(Object.class));
        }

        // 如果傳入的是原生 Java 物件而非 Value
        return new Gson().toJsonTree(target);
    }
}
