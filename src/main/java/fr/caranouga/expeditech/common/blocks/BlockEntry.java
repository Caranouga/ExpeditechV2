package fr.caranouga.expeditech.common.blocks;

public class BlockEntry {
    private final Model model;

    public BlockEntry(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public enum Model {
        BLOCK,
        DUCT,
        MACHINE
        ;
    }
}
