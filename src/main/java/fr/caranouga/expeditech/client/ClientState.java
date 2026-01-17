package fr.caranouga.expeditech.client;

import fr.caranouga.expeditech.client.renderers.MultiblockErrorRenderer;

public class ClientState {
    private static boolean showExpBar = false;
    private static final MultiblockErrorRenderer multiblockErrorRenderer = new MultiblockErrorRenderer();

    public static boolean isShowExpBar() {
        return showExpBar;
    }

    public static void toggleShowExpBar() {
        showExpBar = !showExpBar;
    }

    public static MultiblockErrorRenderer getMultiblockErrorRenderer() {
        return multiblockErrorRenderer;
    }
}