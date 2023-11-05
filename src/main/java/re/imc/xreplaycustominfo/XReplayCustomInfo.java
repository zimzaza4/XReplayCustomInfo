package re.imc.xreplaycustominfo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import re.imc.xreplaycustominfo.listener.ComponentListener;
import re.imc.xreplaycustominfo.listener.PlayerListener;
import re.imc.xreplaycustominfo.record.ExtraInfoRecord;
import re.imc.xreplaycustominfo.task.UpdateInfoTask;
import re.imc.xreplayextendapi.XReplayExtendAPI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class XReplayCustomInfo extends JavaPlugin {

    @Getter
    private String infoFormat = "";

    @Getter
    private String split = ",";

    @Getter
    private static XReplayCustomInfo instance;

    @Getter
    private Map<Player, Map<String, String>> cachedInfo = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        split = getConfig().getString("split-by");
        infoFormat = getConfig().getString("info", "");

        XReplayExtendAPI.getInstance().getCustomRecordManager().registerCustomRecord("extra_info", ExtraInfoRecord.class);

        if (!XReplayExtendAPI.getInstance().isReplayServer()) {
            new UpdateInfoTask().runTaskTimer(this, 0, getConfig().getInt("record-period"));
        } else {
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
            PacketType packetType = PacketType.Play.Server.SET_ACTION_BAR_TEXT;

            if (!packetType.isSupported()) {
                packetType = PacketType.Play.Server.CHAT;
            }
            if (!packetType.isSupported()) {
                packetType = PacketType.Play.Server.SYSTEM_CHAT;
            }

            System.out.println(packetType);
            ProtocolLibrary.getProtocolManager().addPacketListener(new ComponentListener(this, packetType));
        }
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }
}
