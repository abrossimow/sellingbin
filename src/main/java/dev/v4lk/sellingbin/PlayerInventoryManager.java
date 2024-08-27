package dev.v4lk.sellingbin;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInventoryManager {
    private HashMap<UUID, PlayerInventory> playerInventories = new HashMap<>();

    public void addPlayerInventory(UUID playerId, PlayerInventory inventory) {
        playerInventories.put(playerId, inventory);
    }

    public PlayerInventory getPlayerInventory(UUID playerId) {
        if (!playerInventories.containsKey(playerId)) {
            PlayerInventory playerInventory = new PlayerInventory();
            addPlayerInventory(playerId, playerInventory);
            return playerInventory;
        }
        return playerInventories.get(playerId);
    }

    public void removePlayerInventory(UUID playerId) {
        playerInventories.remove(playerId);
    }

    public void save(File file) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(playerInventories);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void load(File file) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            playerInventories = (HashMap<UUID, PlayerInventory>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}