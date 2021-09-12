package pink.zak.mc.leaderboards;

import com.google.common.collect.Maps;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import pink.zak.mc.leaderboards.cache.LeaderboardCache;
import pink.zak.mc.leaderboards.model.Leaderboard;
import pink.zak.mc.leaderboards.storage.RedisStorage;
import pink.zak.mc.leaderboards.storage.UserUpdateStorage;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LeaderboardUpdater {
    private final UserUpdateStorage userUpdateStorage;
    private final RedisStorage redisStorage;
    private final LeaderboardCache leaderboardCache;

    public LeaderboardUpdater(LeaderboardPlugin plugin) {
        this.userUpdateStorage = plugin.getUserUpdateStorage();
        this.redisStorage = plugin.getRedisStorage();
        this.leaderboardCache = plugin.getLeaderboardCache();

        int updatePeriod = plugin.getConfig("settings").integer("redis.update-period");
        LeaderboardPlugin.SCHEDULER_THREAD.scheduleAtFixedRate(() -> plugin.runAsync(this::update), updatePeriod, updatePeriod, TimeUnit.SECONDS);
    }

    private void update() {
        Set<UUID> uuids = this.userUpdateStorage.getUuids();
        Set<UUID> onlineUuids = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        for (Leaderboard leaderboard : this.leaderboardCache.getLeaderboards().values()) {
            Map<UUID, Integer> values;
            if (leaderboard.getTrackingType() == Leaderboard.TrackingType.PLACEHOLDER_API)
                values = this.getPlaceholderValues(leaderboard, leaderboard.isRequireOnline() ? onlineUuids : uuids); // todo cant use identifier
            else
                values = this.getStatisticValues(leaderboard, leaderboard.isRequireOnline() ? onlineUuids : uuids);
            this.redisStorage.addUsers(leaderboard.getIdentifier(), values);
        }
    }

    private Map<UUID, Integer> getStatisticValues(Leaderboard leaderboard, Set<UUID> uuids) {
        Leaderboard.StatisticInfo statisticInfo = leaderboard.getStatisticInfo();
        Map<UUID, Integer> values = Maps.newHashMap();

        for (UUID uuid : uuids) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            Statistic statistic = statisticInfo.getStatistic();
            int score;
            switch (statisticInfo.getStatistic().getType()) {
                case BLOCK:
                case ITEM:
                    score = player.getStatistic(statistic, statisticInfo.getMaterial());
                    break;
                case ENTITY:
                    score = player.getStatistic(statistic, statisticInfo.getEntityType());
                    break;
                case UNTYPED:
                default:
                    score = player.getStatistic(statistic);
            }
            if (score >= leaderboard.getMinValue())
                values.put(uuid, score);
        }
        return values;
    }

    private Map<UUID, Integer> getPlaceholderValues(Leaderboard leaderboard, Set<UUID> uuids) {
        Map<UUID, Integer> values = Maps.newHashMap();
        String placeholder = "%" + leaderboard.getPlaceholder() + "%";
        for (UUID uuid : uuids) {
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                String placeholderValue = PlaceholderAPI.setPlaceholders(player, placeholder);
                int score;
                try {
                    score = Integer.parseInt(placeholderValue);
                } catch (NumberFormatException ex) {
                    LeaderboardPlugin.LOGGER.log(Level.SEVERE, "Placeholder " + placeholder + " returned a non numerical value " + placeholderValue);
                    return Maps.newHashMap();
                }
                if (score >= leaderboard.getMinValue())
                    values.put(uuid, score);
            } catch (Exception ex) {
                LeaderboardPlugin.LOGGER.log(Level.SEVERE, "Error whilst parsing " + placeholder + " for uuid " + uuid, ex);
            }
        }
        return values;
    }
}
