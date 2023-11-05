package re.imc.xreplaycustominfo.task;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.xreplaycustominfo.XReplayCustomInfo;
import re.imc.xreplaycustominfo.record.ExtraInfoRecord;
import re.imc.xreplayextendapi.XReplayExtendAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UpdateInfoTask extends BukkitRunnable {

    @Override
    public void run() {
        Map<String, String> info = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            info.put(player.getName(), PlaceholderAPI.setPlaceholders(player, XReplayCustomInfo.getInstance().getInfoFormat()));
        }
        XReplayExtendAPI.getInstance().addCustomRecord("extra_info", new ExtraInfoRecord(info));
    }
}
