package net.joe.sellingbin;

import java.io.Serializable;

public class PlayerInventory implements Serializable {
    private final ImplementedInventory woodenBin = ImplementedInventory.ofSize(9);
    private final ImplementedInventory ironBin = ImplementedInventory.ofSize(18);
    private final ImplementedInventory diamondBin = ImplementedInventory.ofSize(27);
    private final ImplementedInventory netheriteBin = ImplementedInventory.ofSize(54);

    public ImplementedInventory getWoodenBin() {
        return woodenBin;
    }

    public ImplementedInventory getIronBin() {
        return ironBin;
    }

    public ImplementedInventory getDiamondBin() {
        return diamondBin;
    }

    public ImplementedInventory getNetheriteBin() {
        return netheriteBin;
    }
}