package me.aleksilassila.litematica.printer.v1_21_4;

import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;

import java.util.ArrayDeque;

/**
 * Sliding window over the block interaction packets sent to the server. Servers such as 2b2t only accept
 * a fixed number of them per time frame, so the printer checks its remaining budget before interacting.
 */
public class InteractLimiter {
    private static final ArrayDeque<Long> sentTimes = new ArrayDeque<>();

    /**
     * Records a sent block interaction, whether it came from the printer or from the player clicking.
     */
    public static synchronized void onInteractSent() {
        long now = System.nanoTime() / 1_000_000;
        prune(now);
        sentTimes.addLast(now);
    }

    /**
     * @return How many block interactions can be sent right now without exceeding the limit
     */
    public static synchronized int remaining() {
        int limit = PrinterConfig.INTERACT_LIMIT.getIntegerValue();
        if (limit <= 0) return Integer.MAX_VALUE;

        prune(System.nanoTime() / 1_000_000);
        return Math.max(0, limit - sentTimes.size());
    }

    private static void prune(long now) {
        long cutoff = now - PrinterConfig.INTERACT_TIMEFRAME.getIntegerValue();
        while (!sentTimes.isEmpty() && sentTimes.peekFirst() <= cutoff) {
            sentTimes.pollFirst();
        }
    }
}
