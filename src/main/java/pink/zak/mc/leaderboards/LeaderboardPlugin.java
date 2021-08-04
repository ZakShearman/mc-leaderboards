package pink.zak.mc.leaderboards;

import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.plugin.SpigotPlugin;
import org.bukkit.Bukkit;
import pink.zak.mc.leaderboards.cache.LeaderboardCache;
import pink.zak.mc.leaderboards.placeholderapi.PlaceholderApiHook;
import pink.zak.mc.leaderboards.storage.RedisStorage;
import pink.zak.mc.leaderboards.storage.UserUpdateStorage;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public final class LeaderboardPlugin extends SpigotPlugin {
    public static final ScheduledExecutorService SCHEDULER_THREAD = Executors.newSingleThreadScheduledExecutor();
    public static Logger LOGGER;

    private LeaderboardCache leaderboardCache;
    private UserUpdateStorage userUpdateStorage;
    private RedisStorage redisStorage;

    private LeaderboardUpdater leaderboardUpdater;

    private PlaceholderApiHook placeholderApiHook;

    @Override
    public void onEnable() {
        LOGGER = super.getLogger();
        this.configStore
            .config("settings", Path::resolve, false);

        this.leaderboardCache = new LeaderboardCache(this);

        this.userUpdateStorage = new UserUpdateStorage(this);
        this.redisStorage = new RedisStorage(this);

        this.leaderboardUpdater = new LeaderboardUpdater(this);

        this.registerListeners(
            this.userUpdateStorage
        );

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            this.placeholderApiHook = new PlaceholderApiHook(this);
    }

    @Override
    public void onDisable() {
        SCHEDULER_THREAD.shutdown();
        this.redisStorage.shutdown();

        if (this.placeholderApiHook != null)
            this.placeholderApiHook.unregister();
    }

    public Config getConfig(String name) {
        return this.configStore.getConfig(name);
    }

    public LeaderboardCache getLeaderboardCache() {
        return this.leaderboardCache;
    }

    public UserUpdateStorage getUserUpdateStorage() {
        return this.userUpdateStorage;
    }

    public RedisStorage getRedisStorage() {
        return this.redisStorage;
    }

    public LeaderboardUpdater getLeaderboardUpdater() {
        return this.leaderboardUpdater;
    }
}
