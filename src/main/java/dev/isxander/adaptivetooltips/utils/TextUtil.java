package dev.isxander.adaptivetooltips.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    public static MutableText toText(OrderedText orderedText) {
        MutableText text = Text.empty();

        // constructs a Text by iterating over each character in OrderedText and appending it with its own style
        orderedText.accept((idx, style, codePoint) -> {
            text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
            return true;
        });

        return text;
    }

    public static List<MutableText> toText(Iterable<? extends OrderedText> orderedTextList) {
        List<MutableText> texts = new ArrayList<>();

        for (OrderedText orderedText : orderedTextList) {
            texts.add(toText(orderedText));
        }

        return texts;
    }
}
