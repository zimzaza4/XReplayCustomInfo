package re.imc.xreplaycustominfo.record;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import re.imc.xreplaycustominfo.XReplayCustomInfo;
import re.imc.xreplayextendapi.custom.record.CustomRecord;
import re.imc.xreplayextendapi.custom.record.impl.MessageRecord;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ExtraInfoRecord implements CustomRecord {

    private static Gson GSON = new Gson();
    private Map<String, String> playersInfo;

    public ExtraInfoRecord(String data) {
        playersInfo = GSON.fromJson(data, new TypeToken<Map<String, String>>(){}.getType());
    }

    @Override
    public void execute(Player player) {
        XReplayCustomInfo.getInstance().getCachedInfo().put(player, playersInfo);
    }

    @Override
    public String serialize() {
        return GSON.toJson(playersInfo);
    }
}
