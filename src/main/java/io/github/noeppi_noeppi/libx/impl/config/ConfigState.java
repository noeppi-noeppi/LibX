package io.github.noeppi_noeppi.libx.impl.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import io.github.noeppi_noeppi.libx.config.ValueMapper;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigState {
    
    private final ConfigImpl config;
    private final Map<ConfigKey, Object> values;

    public ConfigState(ConfigImpl config, ImmutableMap<ConfigKey, Object> values) {
        this.config = config;
        this.values = values;
    }
    
    public Object getValue(ConfigKey key) {
        if (!this.values.containsKey(key)) {
            throw new IllegalStateException("Can't get value from config state: Key is invalid.");
        }
        return this.values.get(key);
    }
    
    public void apply() {
        try {
            for (Map.Entry<ConfigKey, Object> entry : this.values.entrySet()) {
                ConfigKey key = entry.getKey();
                Object value = entry.getValue();
                key.field.setAccessible(true);
                key.field.set(null, value);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to insert value into field.", e);
        }
    }
    
    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.values.size());
        for (Map.Entry<ConfigKey, Object> entry : this.values.entrySet()) {
            ConfigKey key = entry.getKey();
            Object value = entry.getValue();
            buffer.writeString(key.field.getDeclaringClass().getName(), 0x7fff);
            buffer.writeString(key.field.getName(), 0x7fff);
            buffer.writeResourceLocation(key.mapperId);
            buffer.writeString(key.elementType == void.class ? "" : key.elementType.getName(), 0x7fff);
            //noinspection unchecked
            ((ValueMapper<Object, ?>) key.mapper).write(value, buffer, key.elementType);
        }
    }

    public void writeToFile() throws IOException {
        if (!Files.isDirectory(this.config.path.getParent())) {
            Files.createDirectories(this.config.path.getParent());
        }
        Writer writer = Files.newBufferedWriter(this.config.path, StandardOpenOption.CREATE);
        writer.write("{\n" + this.applyIndent(this.writeObject(this.values.keySet(), 0), "  ") + "\n}\n");
        writer.close();
    }
    
    public String writeObject(Set<ConfigKey> keys, int pathStrip) {
        List<ConfigKey> simpleKeysSorted = keys.stream()
                .filter(key -> key.path.size() == pathStrip + 1)
                .sorted(ConfigKey.BY_PATH).collect(Collectors.toList());
        
        Map<String, Set<ConfigKey>> subGroups = keys.stream()
                .filter(key -> key.path.size() > pathStrip + 1)
                .collect(Collectors.groupingBy(key -> key.path.get(pathStrip), Collectors.toSet()));
        
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (ConfigKey key : simpleKeysSorted) {
            if (first) {
                first = false;
            } else {
                builder.append(",\n\n");
            }
            key.comment.forEach(line -> builder.append("// ").append(line.replace('\n', ' ')).append("\n"));
            builder.append("\"").append(quote(key.path.get(key.path.size() - 1))).append("\": ");
            Object value = this.values.get(key);
            //noinspection unchecked
            JsonElement json = ((ValueMapper<Object, ?>) key.mapper).toJSON(value, key.elementType);
            builder.append(ConfigImpl.GSON.toJson(json));
        }
        
        List<String> subGroupKeys = subGroups.keySet().stream().sorted().collect(Collectors.toList());
        for (String group : subGroupKeys) {
            if (first) {
                first = false;
            } else {
                builder.append(",\n\n");
            }
            builder.append("\"").append(quote(group)).append("\": {\n");
            builder.append(this.applyIndent(this.writeObject(subGroups.get(group), pathStrip + 1), "  "));
            builder.append("\n}");
        }
        return builder.toString();
    }
    
    private String applyIndent(String str, @SuppressWarnings("SameParameterValue") String indentStr) {
        return indentStr + str.replace("\n", "\n" + indentStr);
    }

    private static String quote(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\t", "\\\t")
                .replace("\b", "\\\b")
                .replace("\n", "\\\n")
                .replace("\r", "\\\r")
                .replace("\f", "\\\f");
    }
}