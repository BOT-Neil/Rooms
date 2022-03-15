package tel.endho.rooms.storage.payloads;

import java.util.UUID;
//unused
public class RankChangePayload extends Payload{
    public UUID uuid;
    public String group;

    public RankChangePayload(UUID uuid, String group){
        this.uuid = uuid;
        this.group = group;
        this.name = "rank-change";
    }
}