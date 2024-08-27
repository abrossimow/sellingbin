package net.joe.sellingbin.bins.iron;

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

public class IronBinBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private long lastSellDay = -1;

    public IronBinBlockEntity(BlockPos pos, BlockState state) {
        super(SellingBinMod.IRON_BIN_BLOCK_ENTITY, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GenericContainerScreenHandler(
                ScreenHandlerType.GENERIC_9X2, syncId, playerInventory,
                SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin(), 2
        );
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.selling-bin.iron_bin");
    }

    public void sellItems(PlayerEntity player) {
        long day = world.getTimeOfDay() / 24000L % 2147483647L;

        if (lastSellDay < day) {
            var playerInventory = SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin();

            List<ItemStack> inventoryCopy = new ArrayList<>(playerInventory.getItems());
            List<Trade> trades = SellingBinMod.trades;

            playerInventory.clear();

            int slotCounter = 0;
            for (ItemStack itemStack : inventoryCopy) {
                boolean isMatched = false;

                for (Trade trade : trades) {
                    if (itemStack.getItem().getTranslationKey().equals(Registries.ITEM.get(new Identifier(trade.getName())).getTranslationKey())) {
                        int sellAmount = trade.getSellAmount();
                        String currencyName = trade.getCurrency();
                        int currencyAmount = trade.getSellPrice();

                        if (itemStack.getCount() >= sellAmount) {
                            isMatched = true;
                            int remainingAmount = itemStack.getCount() % sellAmount;

                            if (remainingAmount != 0) {
                                handleItemRemainders(player, itemStack, slotCounter, remainingAmount);
                            }

                            handleSoldItems(player, currencyName, currencyAmount, itemStack, sellAmount, slotCounter);
                            slotCounter++;
                        }
                    }
                }

                if (!isMatched) {
                    handleUnmatchedItems(player, itemStack, slotCounter);
                }
                slotCounter++;
            }
            lastSellDay = day;
        }
    }

    private void handleItemRemainders(PlayerEntity player, ItemStack itemStack, int slotCounter, int remainingAmount) {
        if (slotCounter > 17) {
            spawnItemEntity(itemStack, remainingAmount);
        } else {
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid())
                    .getIronBin().setStack(slotCounter, new ItemStack(itemStack.getItem(), remainingAmount));
        }
    }

    private void handleSoldItems(PlayerEntity player, String currencyName, int currencyAmount, ItemStack itemStack, int sellAmount, int slotCounter) {
        if (slotCounter > 17) {
            spawnItemEntity(currencyName, currencyAmount, itemStack.getCount() / sellAmount);
        } else {
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin()
                    .setStack(slotCounter, new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), currencyAmount * (itemStack.getCount() / sellAmount)));
        }
    }

    private void handleUnmatchedItems(PlayerEntity player, ItemStack itemStack, int slotCounter) {
        if (slotCounter > 17) {
            spawnItemEntity(itemStack, itemStack.getCount());
        } else {
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().setStack(slotCounter, itemStack);
        }
    }

    private void spawnItemEntity(ItemStack itemStack, int count) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(itemStack.getItem(), count));
        world.spawnEntity(itemEntity);
    }

    private void spawnItemEntity(String currencyName, int currencyAmount, int multiplier) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), currencyAmount * multiplier));
        world.spawnEntity(itemEntity);
    }
}