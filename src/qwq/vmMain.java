package qwq;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class vmMain extends JavaPlugin implements Listener {
    vmMain main;
    private static Economy econ = null;
    public static String prefix=ChatColor.translateAlternateColorCodes('&', "&d[投票] &r");
    Player player;
    int min = 10;
    int vmy, vmn = 0;
    Player[] players = new Player[32];

    @Override
    public void onEnable() {
        main = this;
        setupEconomy();
        getLogger().info("插件加载.");
    }

    @Override
    public void onDisable() {
        getLogger().info("插件卸载.");
    }

//    @EventHandler
//    public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent event){
//        Player player = event.getPlayer();
//        String s = event.getChatMessage();
//        if (s.toLowerCase().contains("/vm")){
//            event.getTabCompletions().add()
//        }
//    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("vm")) {
            if (!(sender instanceof Player)) {
                getLogger().info(prefix + "你必须是玩家!");
                return true;
            }
            if (args.length != 1) {
                return false;
            }
            if (args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("n")) {
                if (player == null) {
                    sender.sendMessage(prefix + "目前没有投票正在进行.");
                    return true;
                }
                int i = 0;
                while (i < 31) {
                    if (players[i] == null) {
                        i = 31;
                    }
                    if (players[i] == ((Player) sender).getPlayer()) {
                        sender.sendMessage(prefix + "你已经投票过了.");
                        return true;
                    }
                    i++;
                }
                if (args[0].equalsIgnoreCase("y")) {
                    vmy++;
                }
                if (args[0].equalsIgnoreCase("n")) {
                    vmn++;
                }
                i = 0;
                while (i < 32) {
                    if (players[i] == null) {
                        players[i] = ((Player) sender).getPlayer();
                        i = 32;
                    } else {
                        i++;
                    }
                }
                sender.sendMessage(prefix + "投票成功.");
                return true;
            }
            if (player != null) {
                sender.sendMessage(prefix + "已经在进行投票了.");
                return true;
            }
            if (Bukkit.getServer().getPlayerExact(args[0]) == null) {
                sender.sendMessage(prefix + "玩家离线: " + args[0]);
                return true;
            }
            if (econ.getBalance(Bukkit.getOfflinePlayer(((Player) sender).getUniqueId())) < 5) {
                sender.sendMessage(prefix + "你没有足够的钱.");
                return true;
            }
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + sender.getName() + " 5");
            Bukkit.getServer().broadcastMessage(prefix + sender.getName() + " 发起投票: 禁言 " + args[0] + " " + min + " 分钟\n" + prefix + "你可以使用 /vm y 接受 或 /vm n 拒绝.");
            player = Bukkit.getPlayer(args[0]);
            BukkitScheduler bukkitScheduler = getServer().getScheduler();
            bukkitScheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    boolean b = true;
                    if (vmn > vmy || 3 > vmy) {
                        Bukkit.getServer().broadcastMessage(prefix + "投票未通过.");
                        b = false;
                    }
                    if (2 < vmn) {
                        Bukkit.getServer().broadcastMessage(prefix + "投票未通过.");
                        b = false;
                    }
                    if (b == true) {
                        Bukkit.getServer().broadcastMessage(prefix + "投票通过.");
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " " + min + "m");
                    }
                    vmn = 0;
                    vmy = 0;
                    player = null;
                    players = new Player[32];
                }
            }, 600L);
        }
        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}

