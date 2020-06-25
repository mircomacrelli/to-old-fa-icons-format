package net.mircomacrelli.fontawesome;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;

public final class Main {
    private static final String HOME = "user.home";
    private static final String UNICODE_DB = "/Library/Application Support/xScope/unicode.db";
    private static final String DELETE_LAST_IMPORT = "DELETE FROM aliases WHERE type = 4";
    private static final String INSERT_ICON = "INSERT INTO aliases (codePoint, name, type) VALUES (?, ?, 4)";

    public static void main(String[] args) throws IOException, SQLException {
        Path db = Paths.get(System.getProperty(HOME), UNICODE_DB).normalize();
        if (Files.notExists(db)) {
            System.err.println(MessageFormat.format("the file ''{0}'' does not exist!", db));
            return;
        }

        var icons = readIcons(args[0]);

        String url = "jdbc:sqlite:" + db;
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement delete = conn.prepareStatement(DELETE_LAST_IMPORT);
             PreparedStatement insert = conn.prepareStatement(INSERT_ICON)) {
            int rows = delete.executeUpdate();
            System.out.println("deleted " + rows + " icons");

            for (var icon : icons.entrySet()) {
                insert.setInt(1, Integer.parseInt(icon.getValue().getUnicode(), 16));
                insert.setString(2, icon.getKey());
                insert.addBatch();
            }

            insert.executeBatch();
        }
    }

    private static Map<String, Icon> readIcons(String path) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(new File(path), new TypeReference<>(){});
    }
}
