package dev.thm.splitdistance;

/**
 * The whole mod, as a pure function, plus its check.
 * No imports on purpose: run the check with `java src/main/java/dev/thm/splitdistance/Cap.java`.
 */
public final class Cap {

    private Cap() {}

    /**
     * @param vanilla        what Minecraft was about to return: min(your setting, server view-distance)
     * @param renderChunks   the configured render cap; <= 0 disables the mod
     * @param onRenderThread whether the caller is the game's renderer
     * @param threadGuard    if true, off-thread callers (map mods) get the uncapped value
     */
    public static int cap(int vanilla, int renderChunks, boolean onRenderThread, boolean threadGuard) {
        if (renderChunks <= 0) return vanilla;
        if (threadGuard && !onRenderThread) return vanilla;
        return Math.min(renderChunks, vanilla);
    }

    public static void main(String[] args) {
        // server allows 32, we render 12
        check(cap(32, 12, true, true) == 12, "renderer capped to 12");
        check(cap(32, 12, false, true) == 32, "map mod thread sees full 32");

        // server only allows 28 -- we take what we get, still render 12
        check(cap(28, 12, true, true) == 12, "renderer capped to 12 on a 28-chunk server");
        check(cap(28, 12, false, true) == 28, "map mod thread sees 28");

        // server is stingier than our render cap: never raise the distance
        check(cap(8, 12, true, true) == 8, "never render further than the server sends");

        // thread guard off: everyone gets the cap
        check(cap(32, 12, false, false) == 12, "guard off caps off-thread callers too");

        // disabled
        check(cap(32, 0, true, true) == 32, "renderChunks=0 disables the mod");
        check(cap(32, -1, false, true) == 32, "negative renderChunks disables the mod");

        System.out.println("ok");
    }

    private static void check(boolean condition, String what) {
        if (!condition) throw new AssertionError(what);
    }
}
