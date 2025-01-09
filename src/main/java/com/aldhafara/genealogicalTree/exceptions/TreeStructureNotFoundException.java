package com.aldhafara.genealogicalTree.exceptions;

public class TreeStructureNotFoundException extends RuntimeException {
    public TreeStructureNotFoundException(String id) {
        super("Tree structure not found for ID: " + id);
    }
}
