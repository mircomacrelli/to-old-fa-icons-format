package net.mircomacrelli.fontawesome;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

public final class Main {
    public static void main(String[] args) throws IOException {
        var icons = readIcons(args[0]);

        StringBuilder sql = new StringBuilder(40 * 1024);
        sql.append("DELETE FROM aliases WHERE type = 4;\n");
        sql.append("INSERT INTO aliases (codePoint, name, type) VALUES\n");

        for (Map.Entry<String, Icon> icon : icons.entrySet()) {
            String codePoint = Integer.toString(Integer.valueOf(icon.getValue().getUnicode(), 16));
            sql.append(MessageFormat.format("  ({0}, ''{1}'', 4),\n", codePoint, icon.getKey()));
        }

        sql.setLength(sql.length() - 2);
        sql.append(';');

        System.out.println(sql);
    }

    private static Map<String, Icon> readIcons(String path) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(new File(path), new TypeReference<>(){});
    }
}
