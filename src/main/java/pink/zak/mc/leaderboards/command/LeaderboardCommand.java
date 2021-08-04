package pink.zak.mc.leaderboards.command;

import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand extends SimpleCommand<CommandSender> {
    private final String noPermissionMessage;
    private final String adminMessage;

    public LeaderboardCommand(SimplePlugin plugin) {
        super(plugin, "leaderboard", true);

        this.noPermissionMessage = this.createNoPermissionMessage();
        this.adminMessage = this.createAdminMessage();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender.hasPermission("leaderboards.admin"))
            sender.sendMessage(this.adminMessage);
        else
            sender.sendMessage(this.noPermissionMessage);
    }

    private String createNoPermissionMessage() {
        return Text.modify(
            "&cLeaderboards by Zak Shearman"
        );
    }

    private String createAdminMessage() {
        return Text.modify(
            "&cLeaderboards by Zak Shearman\n"
                + "\n"
                + "&c/leaderboard full update <leaderboard> - Updates every user for a leaderboard"
        );
    }
}
