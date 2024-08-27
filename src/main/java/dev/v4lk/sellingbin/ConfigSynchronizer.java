package dev.v4lk.sellingbin;
import dev.v4lk.sellingbin.client.SellingBinModClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigSynchronizer {
    public static final Identifier CHANNEL = new Identifier("selling-bin","init");
    public static void server(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        ServerPlayNetworking.send(serverPlayNetworkHandler.player,new SyncPacket(SellingBinMod.trades));
    }
    public static void client(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
        ClientPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE,ConfigSynchronizer::sync);
    }

    private static void sync(SyncPacket syncPacket, ClientPlayerEntity clientPlayerEntity, PacketSender packetSender) {
        SellingBinModClient.matches.removeIf(match -> match instanceof Trade);
        SellingBinModClient.matches.addAll(syncPacket.trades);
    }


    public static class SyncPacket implements FabricPacket {

        public final List<Trade> trades;

        public SyncPacket(PacketByteBuf buf){
            var l = new LinkedList<Trade>();
            var len = buf.readVarInt();
            for(int i=0;i<len;i++){
                var t = new Trade();

                t.setName(buf.readString());
                t.setCurrency(buf.readString());
                t.setSellPrice(buf.readVarInt());
                t.setSellAmount(buf.readVarInt());
                t.setColor(buf.readInt());
                l.add(t);
            }
            trades = l;
        }
        public SyncPacket(List<Trade> trades){
            this.trades = trades;
        }
        @Override
        public void write(PacketByteBuf buf) {
            buf.writeVarInt(trades.size());
            for(var t : trades){
                buf.writeString(t.getName());
                buf.writeString(t.getCurrency());
                buf.writeVarInt(t.getSellPrice());
                buf.writeVarInt(t.getSellAmount());
                buf.writeInt(t.getColor());
            }

        }
        public PacketType<?> getType() {
            return TYPE;
        }

        public static final PacketType<SyncPacket> TYPE = PacketType.create(CHANNEL,SyncPacket::new);
    }
}
