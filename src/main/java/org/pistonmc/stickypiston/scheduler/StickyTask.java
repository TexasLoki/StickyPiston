package org.pistonmc.stickypiston.scheduler;

import org.pistonmc.plugin.Plugin;
import org.pistonmc.scheduler.PistonTask;

public class StickyTask implements PistonTask {

    private static int tasks = 0;

    private int id;

    private Plugin owner;
    private Runnable runnable;
    private long delay;
    private long period;

    private boolean cancelled;

    public StickyTask(Plugin owner, Runnable runnable, long delay, long period) {
        tasks++;
        this.id = tasks;
        this.owner = owner;
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
    }

    public boolean isSync() {
        return true;
    }

    public Plugin getOwner() {
        return owner;
    }

    public Runnable getTask() {
        return runnable;
    }

    public int getId() {
        return id;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Exception ex) {
            owner.getLogger().warning("Task #" + id + " for " + owner.getDescription().getName() + "generated an exception");
            ex.printStackTrace();
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}