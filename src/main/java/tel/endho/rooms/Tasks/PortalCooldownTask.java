package tel.endho.rooms.Tasks;

import tel.endho.rooms.listeners.PortalListener;

public class PortalCooldownTask implements Runnable {
  @Override
  public void run() {
    if (PortalListener.portalcooldowns == null) {
      return;
    }
    if (PortalListener.portalcooldowns.isEmpty()) {
      return;
    }
    if (PortalListener.portalcooldowns.entrySet() == null) {
      return;
    }
    System.out.println("dd+"+ PortalListener.portalcooldowns.entrySet().size());
    if(PortalListener.portalcooldowns.entrySet().size()==0){
      return;
    }try{
      PortalListener.portalcooldowns.entrySet().forEach(entry -> {
      if (entry.getValue() > 40) {
        PortalListener.portalcooldowns.remove(entry.getKey());
      } else {
        PortalListener.portalcooldowns.put(entry.getKey(), entry.getValue() + 1);
      }
    });
    }catch(Exception ignoredendloopException){

    }
    
  }

}