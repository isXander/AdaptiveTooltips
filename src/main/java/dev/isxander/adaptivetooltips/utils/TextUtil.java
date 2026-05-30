/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class TextUtil {
    public static MutableComponent toText(FormattedCharSequence charSequence) {
        MutableComponent text = Component.empty();

        StringBuilder builder = new StringBuilder();
        final Style[] prevStyle = {Style.EMPTY};
        charSequence.accept((idx, style, codePoint) -> {
            if (!style.equals(prevStyle[0])) {
                if (!builder.isEmpty()) {
                    text.append(Component.literal(builder.toString()).setStyle(prevStyle[0]));
                    builder.setLength(0);
                }
                prevStyle[0] = style;
            }
            builder.appendCodePoint(codePoint);

            return true;
        });
        if (!builder.isEmpty()) {
            text.append(Component.literal(builder.toString()).setStyle(prevStyle[0]));
        }

        return text;
    }

}
