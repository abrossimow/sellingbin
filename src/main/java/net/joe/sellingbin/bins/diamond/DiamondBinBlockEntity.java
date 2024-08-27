package net.joe.sellingbin.bins.diamond;

import net.joe.sellingbin.SellingBinMod;
import net.joe.sellingbin.Trade;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DiamondBinBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public long lastSellDay = -1;

    public DiamondBinBlockEntity(BlockPos pos, BlockState state) {
        super(SellingBinMod.DIAMOND_BIN_BLOCK_ENTITY, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GenericContainerScreenHandler(
                ScreenHandlerType.GENERIC_9X3,
                syncId,
                playerInventory,
                SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getDiamondBin(),
                3
        );
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.selling-bin.diamond_bin");
    }

    public void sellItems(PlayerEntity player) {
        long day = (world.getTimeOfDay() / 24000L % 2147483647L);
        if (lastSellDay < day) {
            List<ItemStack> inventoryCopy = new ArrayList<>(
                    SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getDiamondBin().getItems()
            );
            List<Trade> trades = SellingBinMod.trades;
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getDiamondBin().clear();

            int slotIndex = 0;
            for (ItemStack itemStack : inventoryCopy) {
                boolean sold = false;
                for (Trade trade : trades) {
                    if (itemStack.getItem().getTranslationKey().equals(Registries.ITEM.get(new Identifier(trade.getName())).getTranslationKey())) {
                        int sellAmount = trade.getSellAmount();
                        int currencyAmount = trade.getSellPrice();
                        String currencyName = trade.getCurrency();

                        if (itemStack.getCount() >= sellAmount) {
                            sold = true;
                            int remainingAmount = itemStack.getCount() % sellAmount;

                            if (remainingAmount != 0) {
                                handleItemStack(slotIndex, itemStack, remainingAmount, player);
                                slotIndex++;
                            }

                            int currencyStackCount = currencyAmount * (itemStack.getCount() / sellAmount);
                            handleCurrencyStack(slotIndex, currencyName, currencyStackCount, player);
                            break;
                        }
                    }
                }

                if (!sold) {
                    handleItemStack(slotIndex, itemStack, itemStack.getCount(), player);
                }
                slotIndex++;
            }

            lastSellDay = day;
        }
    }

    private void handleItemStack(int slotIndex, ItemStack itemStack, int amount, PlayerEntity player) {
        if (slotIndex > 26) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(itemStack.getItem(), amount));
            world.spawnEntity(itemEntity);
        } else {
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getDiamondBin().setStack(slotIndex, new ItemStack(itemStack.getItem(), amount));
        }
    }

    private void handleCurrencyStack(int slotIndex, String currencyName, int amount, PlayerEntity player) {
        if (slotIndex > 26) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), amount));
            world.spawnEntity(itemEntity);
        } else {
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getDiamondBin().setStack(slotIndex, new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), amount));
        }
    }
}