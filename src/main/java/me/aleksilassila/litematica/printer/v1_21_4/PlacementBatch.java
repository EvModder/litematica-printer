package me.aleksilassila.litematica.printer.v1_21_4;

import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.actions.PrepareLook;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.actions.AirPlaceAction;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.actions.InteractActionImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Places several blocks of one item within a single tick. The item is selected once and, when air placing,
 * swapped to the other hand once for the whole batch instead of for every block.
 */
public class PlacementBatch implements AutoCloseable {
    private final Minecraft client;
    private final LocalPlayer player;
    /**
     * Items that could not be selected this tick, for example while the inventory delay is running.
     */
    private final Set<Item> unavailable = new HashSet<>();
    private int remaining;
    private int placed = 0;
    @Nullable
    private Item item = null;
    private boolean airPlace = false;

    public PlacementBatch(Minecraft client, LocalPlayer player, int limit) {
        this.client = client;
        this.player = player;
        this.remaining = limit;
    }

    public boolean isFull() {
        return remaining <= 0;
    }

    public int getPlaced() {
        return placed;
    }

    /**
     * Cheap check that spares the guides from evaluating blocks this batch would not place anyway.
     *
     * @param candidate The item a block is most likely placed with
     */
    public boolean rejects(Item candidate) {
        return item == null ? unavailable.contains(candidate) : item != candidate;
    }

    /**
     * Places the block if it uses the same item as the rest of the batch.
     * The first block whose item can be selected decides the item.
     *
     * @return True if the block was placed
     */
    public boolean place(PrinterPlacementContext context) {
        if (isFull()) return false;

        if (item == null) {
            if (!begin(context)) return false;
        } else if (context.getItemInHand().getItem() != item || context.isAirPlace != airPlace) {
            return false;
        }

        // Air placements only look somewhere when the block state depends on it.
        if (!airPlace || context.lookDirection != null) {
            new PrepareLook(context).send(client, player);
        }
        InteractAction interact = airPlace ? new AirPlaceAction(context) : new InteractActionImpl(context);
        interact.send(client, player);

        remaining--;
        placed++;
        return true;
    }

    private boolean begin(PrinterPlacementContext context) {
        ItemStack required = context.getItemInHand();
        if (unavailable.contains(required.getItem())) return false;

        // Selecting an item outside the hotbar moves it there, so only try that for the first one each tick.
        boolean atHand = Printer.inventoryManager.isAtHand(player, required);
        if ((!atHand && !unavailable.isEmpty()) || !Printer.inventoryManager.select(required)) {
            unavailable.add(required.getItem());
            return false;
        }

        item = required.getItem();
        airPlace = context.isAirPlace;
        InteractionHand hand = Printer.inventoryManager.getHand(player, required);
        // Stop when the held stack runs out; the next tick selects another one.
        if (!player.getAbilities().instabuild) {
            remaining = Math.min(remaining, player.getItemInHand(hand).getCount());
        }
        if (airPlace) AirPlaceAction.beginBatch(hand);
        return true;
    }

    @Override
    public void close() {
        if (airPlace) AirPlaceAction.endBatch();
    }
}
