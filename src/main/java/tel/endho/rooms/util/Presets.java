package tel.endho.rooms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Presets {
  private static Map<Integer, Preset> presetMap = new HashMap<>();

  public static Map<Integer, Preset> getPresetMap() {
    return presetMap;
  }
  public static Preset gePreset(String string){
    AtomicInteger atomicInteger = new AtomicInteger();
    AtomicBoolean foundPreset = new AtomicBoolean();
    atomicInteger.set(0);
    foundPreset.set(false);
    presetMap.values().forEach(x->{
      if(x.getName().equals(string)){
        foundPreset.set(true);
        //super().return x;
      }
      if(!foundPreset.get()){
        atomicInteger.set(atomicInteger.get()+1);
      }
    });;
    return presetMap.get(atomicInteger.get());
  }
  public static Preset getFirstPreset(){
    return presetMap.get(0);
  }
}
