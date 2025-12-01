package com.roachstudios.critterparade.minigames;

import java.util.function.Supplier;

/**
 * Describes a minigame with its metadata and factory.
 * 
 * <p>This record separates minigame metadata (name, instructions) from the
 * minigame instance itself, following the principle that static information
 * should not require object instantiation.</p>
 * 
 * <p>Using this descriptor pattern ensures:
 * <ul>
 *   <li>Consistent instruction formatting across all minigames</li>
 *   <li>No wasteful temporary object creation for metadata access</li>
 *   <li>Clear separation between configuration and runtime state</li>
 * </ul>
 *
 * @param name the display name for the minigame
 * @param instructions multi-line instructions explaining how to play
 * @param supplier a factory that creates new minigame instances on demand
 */
public record MiniGameDescriptor(
    String name,
    String instructions,
    Supplier<MiniGame> supplier
) {
    /**
     * Creates a new minigame instance.
     *
     * @return a fresh minigame instance ready to play
     */
    public MiniGame create() {
        return supplier.get();
    }
}

