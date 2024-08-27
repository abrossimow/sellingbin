package dev.v4lk.sellingbin.bins.iron;

import dev.v4lk.sellingbin.SellingBinMod;
import dev.v4lk.sellingbin.Trade;
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
    public long lastSellDay = -1;

    public IronBinBlockEntity(BlockPos pos, BlockState state) {
        super(SellingBinMod.IRON_BIN_BLOCK_ENTITY, pos, state);
    }
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin(), 2);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.selling-bin.iron_bin");
    }

    public void sellItems(PlayerEntity player) {
        long day = (world.getTimeOfDay() / 24000L % 2147483647L);
        if (lastSellDay < day) {
            List<ItemStack> inventorycpy = new ArrayList<>(SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().getItems());
            List<Trade> trades = SellingBinMod.trades;
            SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().clear();
            int i = 0;
            for (ItemStack itemStack : inventorycpy) {
                Boolean b = false;
                for (Trade trade : trades) {
                    if (itemStack.getItem().getTranslationKey().equals(Registries.ITEM.get(new Identifier(trade.getName())).getTranslationKey())) {
                        int sellAmount = trade.getSellAmount();
                        String currencyName = trade.getCurrency();
                        int currencyAmount = trade.getSellPrice();
                        if (itemStack.getCount() >= sellAmount) {
                            b = true;
                            int remainingAmount = itemStack.getCount() % sellAmount;
                            if (remainingAmount != 0) {
                                if (i > 17) {
                                    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(itemStack.getItem(), remainingAmount));
                                    world.spawnEntity(itemEntity);
                                } else {
                                    SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().setStack(i, new ItemStack(itemStack.getItem(), remainingAmount));
                                }
                                i++;
                            }
                            if (i > 17) {
                                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), currencyAmount * (itemStack.getCount() / sellAmount)));
                                world.spawnEntity(itemEntity);
                            } else {
                                SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().setStack(i, new ItemStack(Registries.ITEM.get(new Identifier(currencyName)), currencyAmount * (itemStack.getCount() / sellAmount)));
                            }
                        }
                    }
                }
                if (!b) {
                    if (i > 17) {
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                        world.spawnEntity(itemEntity);
                    } else {
                        SellingBinMod.inventoryManager.getPlayerInventory(player.getUuid()).getIronBin().setStack(i, itemStack);
                    }
                }
                i++;
            }
            lastSellDay = day;
        }
    }
}

