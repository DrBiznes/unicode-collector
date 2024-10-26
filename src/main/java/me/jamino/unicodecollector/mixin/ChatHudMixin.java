package me.jamino.unicodecollector.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import me.jamino.unicodecollector.UnicodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    private static boolean isProcessingMessage = false;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"))
    private void onChatMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo info) {
        // Prevent recursive calls
        if (isProcessingMessage) {
            return;
        }

        isProcessingMessage = true;
        try {
            String rawMessage = message.getString();
            if (!rawMessage.isEmpty()) {
                UnicodeCollector.logUnicodeMessage(rawMessage);
            }
        } finally {
            isProcessingMessage = false;
        }
    }
}