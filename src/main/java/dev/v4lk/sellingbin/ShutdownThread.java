package dev.v4lk.sellingbin;

public class ShutdownThread extends Thread {
    @Override
    public void run() {
        SellingBinMod.inventoryManager.save(SellingBinMod.inventoryFile);
    }
}