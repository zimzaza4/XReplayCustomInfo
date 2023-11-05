package re.imc.xreplaycustominfo.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import re.imc.xreplaycustominfo.XReplayCustomInfo;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        XReplayCustomInfo.getInstance().getCachedInfo().remove(event.getPlayer());
    }

}
