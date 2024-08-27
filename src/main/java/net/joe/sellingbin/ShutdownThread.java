package net.joe.sellingbin;

public class ShutdownThread extends Thread {
    @Override
    public void run() {
        SellingBinMod.inventoryManager.save(SellingBinMod.inventoryFile);
    }
}