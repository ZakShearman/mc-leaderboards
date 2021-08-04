package pink.zak.mc.leaderboards.listener;

import com.google.common.collect.Maps;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import pink.zak.mc.leaderboards.LeaderboardPlugin;
import pink.zak.mc.leaderboards.model.Leaderboard;

import java.util.Map;

public class StatisticListener implements Listener {
    private final Map<Statistic, Leaderboard> statisticLeaderboards = Maps.newHashMap();

    public StatisticListener(LeaderboardPlugin plugin) {
        for (Leaderboard leaderboard  :plugin.getLeaderboardCache().getLeaderboards().values()) {
            if (leaderboard.getTrackingType() == Leaderboard.TrackingType.STATISTIC)
                this.statisticLeaderboards.put(leaderboard.getStatisticInfo().getStatistic(), leaderboard);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
        Statistic statistic = event.getStatistic();
        Leaderboard leaderboard = this.statisticLeaderboards.get(statistic);
        if (leaderboard == null)
            return;

    }
}
