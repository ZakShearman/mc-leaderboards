package pink.zak.mc.leaderboards.cache;

import com.google.common.collect.Maps;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.config.ConfigLoader;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import pink.zak.mc.leaderboards.LeaderboardPlugin;
import pink.zak.mc.leaderboards.model.Leaderboard;

import java.util.Map;

public class LeaderboardCache {
    private final Map<String, Leaderboard> leaderboards = Maps.newHashMap();

    public LeaderboardCache(LeaderboardPlugin plugin) {
        Config settings = plugin.getConfig("settings");
        ConfigLoader.reader(settings).keyLoop("leaderboards", identifier -> {
            String path = "leaderboards." + identifier + ".";
            int minValue = settings.integer(path + "min-value");
            boolean requireOnline = settings.bool(path + "require-online");
            Leaderboard.TrackingType trackingType = Leaderboard.TrackingType.valueOf(settings.string(path + "type"));
            Leaderboard.StatisticInfo statisticInfo = trackingType == Leaderboard.TrackingType.STATISTIC ? this.createStatisticInfo(settings, path) : null;
            String placeholder = trackingType == Leaderboard.TrackingType.PLACEHOLDER_API ? settings.string(path + "properties.placeholder") : null;

            Leaderboard leaderboard = new Leaderboard(identifier, minValue, requireOnline, trackingType, statisticInfo, placeholder);
            this.leaderboards.put(identifier, leaderboard);
        });
    }

    private Leaderboard.StatisticInfo createStatisticInfo(Config settings, String path) {
        String propertiesPath = path.concat("properties.");
        Statistic statistic = Statistic.valueOf(settings.string(propertiesPath + "statistic").toUpperCase());
        Statistic.Type type = statistic.getType();
        Material material = type == Statistic.Type.BLOCK || type == Statistic.Type.ITEM ? Material.valueOf(settings.string(propertiesPath + "material")) : null;
        EntityType entityType = type == Statistic.Type.ENTITY ? EntityType.valueOf(settings.string(propertiesPath + "entity")) : null;
        return new Leaderboard.StatisticInfo(statistic, material, entityType);
    }

    public Map<String, Leaderboard> getLeaderboards() {
        return this.leaderboards;
    }

    public Leaderboard getLeaderboard(String identifier) {
        return this.leaderboards.get(identifier);
    }
}
