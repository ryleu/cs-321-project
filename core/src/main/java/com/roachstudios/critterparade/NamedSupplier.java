package com.roachstudios.critterparade;

import java.util.function.Supplier;

/**
 * Pairs a display name with a lazy supplier for a screen (minigame or board).
 * Used in selection menus to show meaningful names instead of indices.
 *
 * @param name the display name for the item
 * @param supplier a supplier that creates new instances on demand
 * @param <T> the type of object the supplier creates
 */
public record NamedSupplier<T>(String name, Supplier<T> supplier) {}

