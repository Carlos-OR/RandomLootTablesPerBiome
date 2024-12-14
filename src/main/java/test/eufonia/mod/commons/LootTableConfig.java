package test.eufonia.mod.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import test.eufonia.EufoniaTest;

import java.io.*;
import java.util.*;

public class LootTableConfig {
    private static Map<String, List<String>> biomesLootTables;

    public LootTableConfig() {
    }

    // Cargar la configuración desde el archivo JSON
    public static void loadConfig() {
        biomesLootTables = null;

        try (InputStream inputStream = LootTableConfig.class.getResourceAsStream(String.format("/data/%s/loot_biomes.json", EufoniaTest.MOD_ID))) {
            System.out.println("\n\nDebería recargarse la variable del map");

            String dirPath = "/eufonia-test", filePath = dirPath + "/loot_biomes.json";
            File dir = new File(dirPath), file = new File(filePath);

            if (!dir.exists()) dir.mkdirs();
            if (!file.exists()) {
                if (inputStream == null) return;
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }

            biomesLootTables = new ObjectMapper().readValue(file, new TypeReference<>() {
            });
            biomesLootTables.forEach((s, strings) -> {
                System.out.println("[CONF] " + s);
                strings.forEach(System.out::println);
            });
            System.out.println("\n\n");
        } catch (Exception e) {
            initializeMap(true);
            System.out.println("\n\nProblemas al cargar el archivo de biomas definidos para loot tables: " + e.getMessage());
        }
    }

    public static void initializeMap(Boolean reload) {
        if (biomesLootTables == null || biomesLootTables.isEmpty() || reload) {
            biomesLootTables = new HashMap<>();
            biomesLootTables.put("default", new ArrayList<>());
        }
    }

    public static List<String> getLootTablesForBiome(String biome) {
        initializeMap(false);
        return biomesLootTables.getOrDefault(biome, biomesLootTables.get("default"));
    }

    public static String getRandomLootTable(String b) {
        initializeMap(false);
        List<String> l = getLootTablesForBiome(b);
        return l.isEmpty() ? "" : l.get(new Random().nextInt(l.size()));
    }
}
