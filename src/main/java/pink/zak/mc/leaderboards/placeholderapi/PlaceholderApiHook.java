package pink.zak.mc.leaderboards.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pink.zak.mc.leaderboards.LeaderboardPlugin;
import pink.zak.mc.leaderboards.storage.RedisStorage;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlaceholderApiHook extends PlaceholderExpansion {
    private final RedisStorage redisStorage;

    private static final DecimalFormat NUMBER_FORMAT;

    static {
        NUMBER_FORMAT = new DecimalFormat();
        NUMBER_FORMAT.setGroupingUsed(true);
        NUMBER_FORMAT.setGroupingSize(3);
    }

    public PlaceholderApiHook(LeaderboardPlugin plugin) {
        this.redisStorage = plugin.getRedisStorage();

        this.register();
    }

    @Override
    public String onRequest(OfflinePlayer p, String placeholder) {
        String[] splitPlaceholder = placeholder.split("_");
        if (splitPlaceholder[0].equals("score")) {
            String leaderboardId = splitPlaceholder[1];
            int position = Integer.parseInt(splitPlaceholder[2]);
            int score = this.redisStorage.getScore(leaderboardId, position);
            return score < 0 ? "N/A" : NUMBER_FORMAT.format(score);
        } else if (splitPlaceholder[0].equals("user")) {
            String leaderboardId = splitPlaceholder[1];
            int position = Integer.parseInt(splitPlaceholder[2]);
            UUID uuid = this.redisStorage.getUuidAt(leaderboardId, position);
            return uuid == null ? "N/A" : Bukkit.getOfflinePlayer(uuid).getName();
        } else {
            return null;
        }
    }

    @Override
    public String getIdentifier() {
        return "leaderboard";
    }

    @Override
    public String getAuthor() {
        return "Zak Shearman";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
