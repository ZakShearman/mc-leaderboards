package pink.zak.mc.leaderboards.storage;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import pink.zak.mc.leaderboards.LeaderboardPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserUpdateStorage implements Listener {
    private final Gson gson = new Gson();
    private final Path path;
    private Set<UUID> uuids;

    public UserUpdateStorage(LeaderboardPlugin plugin) {
        Config settings = plugin.getConfig("settings");
        boolean saveUuids = settings.bool("save-uuids.enabled");

        if (saveUuids) {
            int updatePeriod = settings.integer("save-uuids.update-period");

            this.path = plugin.getDataFolder().toPath().resolve("updates.json");
            this.uuids = this.load();

            LeaderboardPlugin.SCHEDULER_THREAD.scheduleAtFixedRate(this::save, updatePeriod, updatePeriod, TimeUnit.SECONDS);
        } else {
            this.path = null;
            this.uuids = Sets.newHashSet();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        this.uuids.add(event.getPlayer().getUniqueId());
    }

    public Set<UUID> getUuids() {
        Set<UUID> uuids = this.uuids;
        this.uuids = this.createNewSet();
        return uuids;
    }

    private Set<UUID> createNewSet() {
        Set<UUID> newSet = Sets.newHashSet();
        for (Player player : Bukkit.getOnlinePlayers() )
            newSet.add(player.getUniqueId());
        return newSet;
    }

    private Set<UUID> load() {
        FileReader reader;
        try {
            reader = new FileReader(this.path.toFile());
        } catch (FileNotFoundException ex) {
            return this.createNewSet();
        }
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
        return this.gson.fromJson(jsonObject.get("").getAsString(), new TypeToken<HashSet<UUID>>(){}.getType());
    }

    private void save() {
        if (!Files.exists(this.path)) {
            try {
                this.path.toFile().createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try (Writer writer = Files.newBufferedWriter(this.path)){
            JsonObject json = new JsonObject();
            json.addProperty("", this.gson.toJson(this.uuids));
            this.gson.toJson(json, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
