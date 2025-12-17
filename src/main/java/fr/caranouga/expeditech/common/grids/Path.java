package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.common.te.custom.duct.DuctTE;

import java.util.List;

public class Path<D extends DuctTE<?, D>> {
    private final List<Node<D>> path;

    public Path(List<Node<D>> path) {
        this.path = path;
    }

    public List<Node<D>> getDucts() {
        return path;
    }
}
