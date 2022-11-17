package tel.endho.rooms;

import java.util.Map;
import java.util.UUID;

public class RoomWorldGroup {
  private UUID groupUuid;
  private String groupName;
  private Integer groupweight;//blocked 0, trusted 1, member 2,custom groups, default 1000
  private Map<String,Boolean> flags;
}
