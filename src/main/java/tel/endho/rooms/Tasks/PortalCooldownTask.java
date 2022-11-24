package tel.endho.rooms.Tasks;

import tel.endho.rooms.listeners.PortalListener;

public class PortalCooldownTask implements Runnable {
  @Override
  public void run() {
    PortalListener.portalcooldowns.entrySet().forEach(entry->{
      if(entry.getValue()>40){
        PortalListener.portalcooldowns.remove(entry.getKey());
      }else{
        PortalListener.portalcooldowns.put(entry.getKey(),entry.getValue()+1);
      }
    });
  }

}