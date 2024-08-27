package dev.v4lk.sellingbin.client;

import dev.v4lk.sellingbin.ConfigSynchronizer;
import dev.v4lk.sellingbin.Trade;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SellingBinModClient implements ClientModInitializer {
    public static List<Trade> matches = new ArrayList<>();
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register(ConfigSynchronizer::client);
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            for(var item: matches){
                if(item.matches(stack)){
                    var addtext = Text.literal(String.format(I18n.translate("selling-bin.tooltip.sell"),
                            item.getSellAmount(),
                            item.getSellPrice(),
                            I18n.translate(Registries.ITEM.get(new Identifier(item.getCurrency())).getTranslationKey())).substring("Format error: ".length()));
                    lines.add(addtext.setStyle(Style.EMPTY.withColor(item.getColor())));
                }
            }
        });
    }
}
