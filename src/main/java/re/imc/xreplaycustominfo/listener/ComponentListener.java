package re.imc.xreplaycustominfo.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.wrappers.ComponentConverter;
import re.imc.xreplaycustominfo.XReplayCustomInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class ComponentListener extends PacketAdapter {
    public ComponentListener(Plugin plugin, PacketType type) {
        super(plugin, ListenerPriority.HIGHEST, type);
    }

    private static final Pattern ANY_COLOR_PATTERN = Pattern.compile("(?i)[&§][0-9a-folkrnm]");

    @Override
    public void onPacketSending(PacketEvent event) {

        PacketContainer packet = event.getPacket();

        if (event.getPacket().getType() == PacketType.Play.Server.SYSTEM_CHAT) {
            StructureModifier<Boolean> booleans = packet.getBooleans();
            if (booleans.size() == 1) {
                if (!booleans.read(0)) {
                    return;
                }
            } else if (packet.getIntegers().read(0) != EnumWrappers.ChatType.GAME_INFO.getId()) {
                return;
            }

        }

        if (event.getPacket().getType() == PacketType.Play.Server.CHAT) {
            if (packet.getChatTypes().read(0) != EnumWrappers.ChatType.GAME_INFO
                    && (packet.getBytes().size() < 1 || packet.getBytes().read(0) != 2)) {
                return;
            }
        }
        StructureModifier<BaseComponent> modifier = event.getPacket().getSpecificModifier(BaseComponent.class);

        String fullMsg;
        BaseComponent baseComponent = modifier.readSafely(0);

        if (baseComponent == null) {
            StructureModifier<BaseComponent[]> spigotModifier = event.getPacket().getSpecificModifier(BaseComponent[].class);
            StringBuilder builder = new StringBuilder();
            BaseComponent[] components = spigotModifier.readSafely(0);
            if (components == null || components.length == 0) {
                StructureModifier<WrappedChatComponent> componentModifier = event.getPacket().getChatComponents();
                components =  ComponentConverter.fromWrapper(componentModifier.read(0));
                System.out.println("act 3");
            }

            if (components == null || components.length == 0) {
                return;
            }

            for (BaseComponent component : components) {
                builder.append(component.toLegacyText());
            }
            fullMsg = builder.toString();

        } else {
            fullMsg = baseComponent.toLegacyText();
        }
        String[] values = ANY_COLOR_PATTERN.matcher(fullMsg).replaceAll("").split(XReplayCustomInfo.getInstance().getSplit());
        System.out.println("ACTV:" + Arrays.toString(values));
        if (values.length == 0) {
            return;
        }
        String name = values[0];

        Map<String, String> info = XReplayCustomInfo.getInstance().getCachedInfo().get(event.getPlayer());

        if (info == null) {
            return;
        }
        String msg = info.getOrDefault(name, "");
        fullMsg = fullMsg + msg;
        TextComponent component = new TextComponent(fullMsg);
        if (event.getPacket().getType() == PacketType.Play.Server.SYSTEM_CHAT) {
            packet.getStrings().write(0, ComponentSerializer.toString(component));
        } else {
            packet.getChatComponents().write(0, ComponentConverter.fromBaseComponent(component));
        }
    }
}
