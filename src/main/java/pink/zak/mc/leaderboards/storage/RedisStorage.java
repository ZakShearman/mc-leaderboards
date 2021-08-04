package pink.zak.mc.leaderboards.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hyfe.simplespigot.config.Config;
import pink.zak.mc.leaderboards.LeaderboardPlugin;
import pink.zak.mc.leaderboards.cache.LeaderboardCache;
import pink.zak.mc.leaderboards.model.Leaderboard;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RedisStorage {
    private final String prefix;
    private final Jedis jedis;

    private final Map<String, String> setNames;

    public RedisStorage(LeaderboardPlugin plugin) {
        Config settings = plugin.getConfig("settings");
        String address = settings.string("redis.address");
        int port = settings.integer("redis.port");

        this.prefix = settings.string("redis.prefix");

        this.jedis = new Jedis(address, port);
        this.setNames = this.createSetNames(plugin.getLeaderboardCache());
    }

    public void addUsers(String placeholder, Map<UUID, Integer> data) {
        String setName = this.setNames.get(placeholder);
        for (Map.Entry<UUID, Integer> entry : data.entrySet()) {
            this.jedis.zadd(setName, entry.getValue(), entry.getKey().toString());
        }
    }

    public UUID getUuidAt(String leaderboardId, int position) {
        position -= 1;
        Set<Tuple> results = this.jedis.zrevrangeWithScores(this.setNames.get(leaderboardId), position, position);

        if (results.isEmpty())
            return null;
        return UUID.fromString(results.iterator().next().getElement());

    }

    public int getScore(String leaderboardId, int position) {
        position -= 1;
        Set<Tuple> results = this.jedis.zrevrangeWithScores(this.setNames.get(leaderboardId), position, position);

        if (results.isEmpty())
            return -1;
        return (int) results.iterator().next().getScore();
    }

    public int getScore(String leaderboardId, UUID uuid) {
        Double score = this.jedis.zscore(this.setNames.get(leaderboardId), uuid.toString());
        if (score == null)
            return -1;
        return score.intValue();
    }

    public List<Tuple> getTop(String leaderboardId, int amount) {
        Set<Tuple> results = this.jedis.zrevrangeWithScores(this.setNames.get(leaderboardId), 0, amount - 1);

        List<Tuple> orderedResults = Lists.newArrayList(results);
        Collections.sort(orderedResults);

        return orderedResults;
    }

    private Map<String, String> createSetNames(LeaderboardCache leaderboardCache) {
        Map<String, String> setNames = Maps.newHashMap();

        for (Leaderboard leaderboard : leaderboardCache.getLeaderboards().values()) {
            setNames.put(leaderboard.getIdentifier(), this.prefix.concat(leaderboard.getIdentifier()));
        }
        return setNames;
    }

    public void shutdown() {
        this.jedis.close();
    }
}
