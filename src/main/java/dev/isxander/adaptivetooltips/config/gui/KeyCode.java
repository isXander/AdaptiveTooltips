package dev.isxander.adaptivetooltips.config.gui;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyCode {
    class KeyCodeImpl extends SimpleOptionFactory<KeyCode, Integer> {
        @Override
        protected ControllerBuilder<Integer> createController(KeyCode annotation, ConfigField<Integer> field, OptionAccess storage, Option<Integer> option) {
            return () -> new KeyCodeController(option);
        }
    }
}
