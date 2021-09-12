package pink.zak.mc.leaderboards.model;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class Leaderboard {
    private final String identifier;
    private final int minValue;
    private final boolean requireOnline;
    private final TrackingType trackingType;
    private final StatisticInfo statisticInfo;
    private final String placeholder;

    public Leaderboard(String identifier, int minValue, boolean requireOnline, TrackingType trackingType, StatisticInfo statisticInfo, String placeholder) {
        this.identifier = identifier;
        this.minValue = minValue;
        this.requireOnline = requireOnline;
        this.trackingType = trackingType;
        this.statisticInfo = statisticInfo;
        this.placeholder = placeholder;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public boolean isRequireOnline() {
        return this.requireOnline;
    }

    public TrackingType getTrackingType() {
        return this.trackingType;
    }

    public StatisticInfo getStatisticInfo() {
        return this.statisticInfo;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public static class StatisticInfo {
        private final Statistic statistic;
        private final Material material;
        private final EntityType entityType;

        public StatisticInfo(Statistic statistic, Material material, EntityType entityType) {
            this.statistic = statistic;
            this.material = material;
            this.entityType = entityType;
        }

        public Statistic getStatistic() {
            return this.statistic;
        }

        public Material getMaterial() {
            return this.material;
        }

        public EntityType getEntityType() {
            return this.entityType;
        }
    }

    public enum TrackingType {
        STATISTIC,
        PLACEHOLDER_API
    }
}
