package dev.leon.splitdistance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/**
 * ponytail: vanilla OptionsSubScreen + OptionInstance. Same widgets, layout, scrolling and
 * Done button the vanilla video settings screen uses, for two settings' worth of code.
 * No Cloth Config, no Fabric API.
 */
public class ConfigScreen extends OptionsSubScreen {

    private final OptionInstance<Integer> renderChunks = new OptionInstance<>(
            "splitdistance.option.renderChunks",
            OptionInstance.cachedConstantTooltip(Component.translatable("splitdistance.option.renderChunks.tooltip")),
            (caption, value) -> Component.translatable("options.generic_value", caption,
                    value == 0
                            ? Component.translatable("splitdistance.option.renderChunks.off")
                            : Component.translatable("options.chunks", value)),
            new OptionInstance.IntRange(0, Config.MAX_RENDER_CHUNKS),
            Config.renderChunks(),
            value -> {});

    private final OptionInstance<Boolean> threadGuard = OptionInstance.createBoolean(
            "splitdistance.option.threadGuard",
            OptionInstance.cachedConstantTooltip(Component.translatable("splitdistance.option.threadGuard.tooltip")),
            Config.threadGuard());

    public ConfigScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options, Component.translatable("splitdistance.config.title"));
    }

    @Override
    protected void addOptions() {
        this.list.addSmall(renderChunks, threadGuard);
    }

    @Override
    public void removed() {
        boolean changed = Config.set(renderChunks.get(), threadGuard.get());
        // Rebuilding every section is what vanilla does when you drag the render distance slider.
        if (changed && this.minecraft != null && this.minecraft.level != null) {
            this.minecraft.levelRenderer.allChanged();
        }
        super.removed();
    }
}
