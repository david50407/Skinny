package tw.davy.minecraft.skinny.providers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;

/**
 * @author Davy
 */
public class MojangProvider extends LegacyProvider {
    @Override
    public SignedSkin getSkinData(final String name) {
        final String uuid = getUUID(name);
        if (uuid == null)
            return null;

        final SignedSkin cachedSkin = super.getSkinData(uuid);
        final OfflinePlayer player = getPlayer(name);
        if (cachedSkin != null && player != null && player.getLastPlayed() < getCacheTime(uuid))
            return cachedSkin;

        final String data = readUrl("https://sessionserver.mojang.com/session/minecraft/profile/"
                + uuid + "?unsigned=false");

        if (data.isEmpty() || data.contains("\"error\""))
            return cachedSkin;

        try {
            final JSONObject jsonData = (JSONObject) new JSONParser().parse(data);
            final JSONArray properties = (JSONArray) jsonData.get("properties");
            for (final Object property : properties) {
                final JSONObject prop = (JSONObject) property;
                if (!prop.get("name").equals("textures"))
                    continue;

                final SignedSkin skin = new SignedSkin((String) prop.get("value"), (String) prop.get("signature"));
                createCache(uuid, skin);

                return skin;
            }
        } catch (ParseException ignored) {
        }

        return cachedSkin;
    }

    @Override
    protected File getSkinFolder() {
        return new File(Skinny.getInstance().getDataFolder(),"mojang_caches");
    }

    private void createCache(final String uuid, final SignedSkin skin) {
        if (!getSkinDir(uuid).exists())
            getSkinDir(uuid).mkdir();
        writeData(new File(getSkinDir(uuid), "value.dat"), skin.getValue());
        writeData(new File(getSkinDir(uuid), "signature.dat"), skin.getSignature());
        writeData(new File(getSkinDir(uuid), "timestamp"), String.valueOf(Instant.now().toEpochMilli()));
    }

    private static OfflinePlayer getPlayer(final String name) {
        for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }

        return null;
    }

    private void writeData(final File file, final String data) {
        try {
            final BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            buf.write(data);
            buf.close();
        } catch (IOException ignored) {
        }
    }

    private long getCacheTime(final String name) {
        final String data = readData(new File(getSkinDir(name), "timestamp"));
        try {
            return Long.parseLong(data) + TimeUnit.MINUTES.toMillis(30);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String getUUID(final String name) {
        final String data = readUrl("https://api.mojang.com/users/profiles/minecraft/" + name);

        if (data.isEmpty() || data.contains("\"error\""))
            return null;

        final int idPos = data.indexOf("\"id\":");
        final int idStartPos = data.indexOf("\"", idPos + 5) + 1;
        final int idEndPos = data.indexOf("\"", idStartPos);
        return data.substring(idStartPos, idEndPos);
    }

    private static String readUrl(final String url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Minecraft");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            String line;
            final StringBuilder output = new StringBuilder();
            BufferedReader buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = buf.readLine()) != null)
                output.append(line);

            buf.close();

            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
