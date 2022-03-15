package tel.endho.rooms.storage;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;

import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tel.endho.rooms.*;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Redis {
    public static Boolean isLoaded;
    private UUID serverUuid;//to prevent duplicate actions
    private String bungeesrvname;//, host, password;
    //private int port;
    private static RedisClient redisClient;
    //StatefulRedisConnection<String, String> connection;
    //RedisStringAsyncCommands<String, String> async = connection.async();
    RedisPubSubAsyncCommands<String, String> pubSubAsyncCommands;
    StatefulRedisConnection<String, String> StatefulredisCommands;
    //RedisAsyncCommands<String, String> asyncCommands;


    public void initRedis(String bungeesrvname, String host, String password, int port) throws ExecutionException, InterruptedException, TimeoutException {
        serverUuid = UUID.randomUUID();
        this.bungeesrvname = bungeesrvname;
        //this.host = host;
        //this.password = password;
        //this.port = port;
        RedisURI redisUri = RedisURI.Builder.redis(host)
                .withHost(host)
                .withSsl(false)
                .withPassword((CharSequence) password)
                .withPort(port)
                .withDatabase(0)
                .build();
        System.out.println(redisUri.toString());
        redisClient = RedisClient.create(redisUri);
        //connection = redisClient.connect();
        //asyncCommands = connection.async();

        //asyncCommands.pubsubChannels().get().add("Rooms"+bungeesrvname);
        //syncCommands.set("key", "Hello, Redis!");

        try {
            redis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //connection.close();
        //redisClient.shutdown();
    }
    public Boolean isLoaded(){
        return isLoaded;
    }
    public void redis(){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                pubSubAsyncCommands= redisClient.connectPubSub().async();
                StatefulredisCommands= redisClient.connect();
                //pubSubAsyncCommands.subscribe("Rooms"+bungeesrvname);
                pubSubAsyncCommands.getStatefulConnection().addListener(new RedisPubSubListener<>() {

                    @Override
                    public void message(String channel, String message) {
                        if (channel.equals("Rooms" + bungeesrvname)) {
                            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
                            ByteArrayDataInput byteArray = ByteStreams.newDataInput(bytes);
                            try{
                                //System.out.println("bytearry "+byteArray.readUTF());
                                String tag = byteArray.readUTF();
                                if (tag.equals("global_update")) {
                                    UUID worlduuid = UUID.fromString(byteArray.readUTF());
                                    String lastserver = byteArray.readUTF();
                                    String ownername = byteArray.readUTF();
                                    Long systime = Long.valueOf(byteArray.readUTF());
                                    int maxPlayers = Integer.parseInt(byteArray.readUTF());
                                    GlobalRoomWorlds.getGlobalRoomWorlds().put(worlduuid, new GlobalRoomWorld(worlduuid, ownername, lastserver, systime));
                                    //GlobalRoomWorlds.putRoom(worlduuid, new GlobalRoomWorld(worlduuid, ownername, lastserver, systime));
                                }
                                if (tag.equals("delete")) {
                                    UUID serveruuid = UUID.fromString(byteArray.readUTF());
                                    if(serveruuid==serverUuid)return;
                                    UUID worlduuid = UUID.fromString(byteArray.readUTF());
                                    RoomWorlds.getRoomWolrds().remove(worlduuid);
                                }
                                if (tag.equals("removemember")){
                                    UUID serveruuid = UUID.fromString(byteArray.readUTF());
                                    if(serveruuid==serverUuid)return;
                                    UUID worlduuid = UUID.fromString(byteArray.readUTF());
                                    UUID memberuuid = UUID.fromString(byteArray.readUTF());
                                    if(RoomWorlds.isRoomWorld(worlduuid)){
                                        RoomWorlds.getRoomWorldUUID(worlduuid).removeMember(memberuuid);
                                    }

                                }
                                if (tag.equals("removetrusted")){

                                }
                                if (tag.equals("removeblocked")){

                                }
                                if(tag.equals("clearmembers")){
                                    UUID serveruuid = UUID.fromString(byteArray.readUTF());
                                    if(serveruuid==serverUuid)return;
                                    UUID worlduuid = UUID.fromString(byteArray.readUTF());
                                    if(RoomWorlds.isRoomWorld(worlduuid)){
                                        RoomWorlds.getRoomWorldUUID(worlduuid).clearMembers(false);
                                    }
                                }
                                if(tag.equals("teleportplayer")){
                                    UUID serveruuid = UUID.fromString(byteArray.readUTF());
                                    if(serveruuid==serverUuid)return;
                                    String servername = byteArray.readUTF();
                                    if (!servername.equals(bungeesrvname))return;

                                    UUID worlduuid = UUID.fromString(byteArray.readUTF());
                                    if(RoomWorlds.isRoomWorld(worlduuid)){
                                        RoomWorlds.getRoomWorldUUID(worlduuid).clearMembers(false);
                                    }
                                }
                                //todo clear trust and blocked
                            }catch (Exception exception){}



                        }
                    }

                    @Override
                    public void message(String pattern, String channel, String message) {
                        System.out.println(pattern);
                        System.out.println(channel);
                        System.out.println(message);
                    }

                    @Override
                    public void subscribed(String channel, long count) {

                    }

                    @Override
                    public void psubscribed(String pattern, long count) {

                    }

                    @Override
                    public void unsubscribed(String channel, long count) {

                    }

                    @Override
                    public void punsubscribed(String pattern, long count) {

                    }
                });
                pubSubAsyncCommands.subscribe("Rooms"+bungeesrvname);
            }

        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }
    public void teleportPlayer(Player player, String server, UUID worlduuid){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("teleportplayer");
                out.writeUTF(worlduuid.toString());
                out.writeUTF(server);
                out.writeUTF(player.getUniqueId().toString());
                out.writeUTF(String.valueOf(System.currentTimeMillis()));
                //pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
                String storeStr = new String(out.toByteArray(), StandardCharsets.UTF_8);
                StatefulredisCommands.async().publish("Rooms"+bungeesrvname, storeStr);
                //pubSubAsyncCommands.publish("Rooms"+bungeesrvname,out.toString());
                //asyncCommands.set("Rooms"+bungeesrvname,out.toString());
            };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }
    /*public void insertGlobalServerInfo(){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {

                int maxPlayers =Rooms.getPlugin().getServer().getMaxPlayers();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("global_serverinfo");
                out.writeUTF(roomWorld.getWorldUUID().toString());
                out.writeUTF(bungeesrvname);
                out.writeUTF(roomWorld.getOwnerName());
                out.writeUTF(String.valueOf(System.currentTimeMillis()));
                //pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
                String storeStr = new String(out.toByteArray(), StandardCharsets.UTF_8);
                StatefulredisCommands.async().publish("Rooms"+bungeesrvname, storeStr);
                //pubSubAsyncCommands.publish("Rooms"+bungeesrvname,out.toString());
                //asyncCommands.set("Rooms"+bungeesrvname,out.toString());
            };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }*/
    public void insertGlobal(RoomWorld roomWorld){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                String maxPlayers =Integer.toString(Rooms.getPlugin().getServer().getMaxPlayers());
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("global_update");
                out.writeUTF(roomWorld.getWorldUUID().toString());
                out.writeUTF(bungeesrvname);
                out.writeUTF(roomWorld.getOwnerName());
                out.writeUTF(String.valueOf(System.currentTimeMillis()));
                out.writeUTF(maxPlayers);
                //pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
                String storeStr = new String(out.toByteArray(), StandardCharsets.UTF_8);
                StatefulredisCommands.async().publish("Rooms"+bungeesrvname, storeStr);
                //pubSubAsyncCommands.publish("Rooms"+bungeesrvname,out.toString());
                //asyncCommands.set("Rooms"+bungeesrvname,out.toString());
          };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }
    public void delete(UUID worlduuid){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(serverUuid.toString());
                out.writeUTF("delete");
                out.writeUTF(worlduuid.toString());
                out.writeUTF(bungeesrvname);
                pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
            };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }
    public void removeMember(UUID worlduuid, UUID playerUUID){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(serverUuid.toString());
                out.writeUTF("remove_member");
                out.writeUTF(worlduuid.toString());
                out.writeUTF(playerUUID.toString());
                out.writeUTF(bungeesrvname);
                pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
            };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }
    public void clearMembers(UUID worlduuid){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(serverUuid.toString());
                out.writeUTF("clearmembers");
                out.writeUTF(worlduuid.toString());
                out.writeUTF(bungeesrvname);
                pubSubAsyncCommands.set("Rooms"+bungeesrvname,out.toString());
            };};
        r.runTaskAsynchronously(Rooms.getPlugin());
    }
    /*private Gson gson = new Gson();

    public String toJson (Payload payload){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("matchme");
        out.writeUTF(s);
        out.writeUTF(p.getName());
        String json = gson.toJson(payload);
        JsonObject obj =
        obj.put("name", payload.name);
        obj.put("payload", json.toString());

        return obj.toString();
    }

    public Payload fromJson (String json){
        JsonObject obj = new JSONObject(json);
        String name = obj.get("name");
        String payloadJson = obj.get("payload");

        if(name.equals("rank-change"){
            return gson.fromJson(payloadJson,  RankChangePayload.class);
        }
    }*/
}
