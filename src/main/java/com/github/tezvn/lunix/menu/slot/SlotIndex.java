package com.github.tezvn.lunix.menu.slot;

public class SlotIndex {

    private final int slot;

    private final int bukkitSlot;

    private final int row;

    private final int column;

    public SlotIndex(int slot, int bukkitSlot, int row, int column) {
        this.slot = slot;
        this.bukkitSlot = bukkitSlot;
        this.row = row;
        this.column = column;
    }

    /**
     * Get the slot by type
     *
     * @param type Type to get
     */
    public int get(SlotType type) {
        return switch (type) {
            case ORDER -> slot;
            case BUKKIT -> bukkitSlot;
            case ROW -> row;
            case COLUMN -> column;
        };
    }

    @Override
    public String toString() {
        return "SlotIndex{" +
                "slot=" + slot +
                ", bukkitSlot=" + bukkitSlot +
                '}';
    }
}