package org.pistonmc.stickypiston.scheduler;

import com.google.common.base.Preconditions;
import org.pistonmc.plugin.Plugin;
import org.pistonmc.scheduler.Duration;
import org.pistonmc.scheduler.PistonScheduler;
import org.pistonmc.scheduler.PistonTask;
import org.pistonmc.stickypiston.StickyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StickyScheduler implements PistonScheduler {

    private StickyServer server;
    private Map<Integer, PistonTask> tasks;
    private Map<Long, List<PistonTask>> tickTasks;

    public StickyScheduler(StickyServer server) {
        this.server = server;
        this.tasks = new ConcurrentHashMap<>();
        this.tickTasks = new ConcurrentHashMap<>();
    }

    public PistonTask create(Plugin plugin, Runnable runnable, long delay, long period, boolean async) {
        validate(plugin, runnable);

        PistonTask task;
        if(async) {
            task = new StickyAsyncTask(plugin, runnable, delay, period);
        } else {
            task = new StickyTask(plugin, runnable, delay, period);
        }

        tasks.put(task.getId(), task);

        long tick = server.getTick() + 1 + delay;
        List<PistonTask> tasks = tickTasks.get(tick);
        if(tasks == null) {
            tasks = new ArrayList<>();
            tickTasks.put(tick, tasks);
        }

        tasks.add(task);
        return task;
    }

    public PistonTask run(Plugin plugin, Runnable runnable) {
        return delay(plugin, runnable, 0);
    }

    public PistonTask runAsync(Plugin plugin, Runnable runnable) {
        return delayAsync(plugin, runnable, 0);
    }

    public PistonTask delay(Plugin plugin, Runnable runnable, Duration duration, long delay, boolean async) {
        Preconditions.checkArgument(delay > -1, "Delay cannot be negative");
        return create(plugin, runnable, duration.getTicks(delay), -1, async);
    }

    public PistonTask delay(Plugin plugin, Runnable runnable, long delay) {
        return delay(plugin, runnable, Duration.TICKS, delay);
    }

    public PistonTask delay(Plugin plugin, Runnable runnable, Duration duration, long delay) {
        return delay(plugin, runnable, duration, delay, false);
    }

    public PistonTask delayAsync(Plugin plugin, Runnable runnable, long delay) {
        return delayAsync(plugin, runnable, Duration.TICKS, delay);
    }

    public PistonTask delayAsync(Plugin plugin, Runnable runnable, Duration duration, long delay) {
        return delay(plugin, runnable, duration, delay, true);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, Duration duration, long delay, long period, Long cancel, boolean async) {
        Preconditions.checkArgument(delay > -1, "Delay cannot be negative");
        Preconditions.checkArgument(period > 0, "Period cannot be less than 1");
        final PistonTask task = create(plugin, runnable, duration.getTicks(delay), duration.getTicks(period), async);
        if(cancel != null) {
            Preconditions.checkArgument(cancel > 0, "Cancellation delay cannot be less than 1");
            create(plugin, new Runnable() {
                @Override
                public void run() {
                    task.cancel();
                }
            }, duration.getTicks(cancel), -1, async);
        }

        return task;
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, long delay, long period, Long cancel) {
        return repeat(plugin, runnable, Duration.TICKS, delay, period, cancel);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, Duration duration, long delay, long period, Long cancel) {
        return repeat(plugin, runnable, duration, delay, period, cancel, false);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, long delay, long period, Long cancel) {
        return repeatAsync(plugin, runnable, Duration.TICKS, delay, period, cancel);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, Duration duration, long delay, long period, Long cancel) {
        return repeat(plugin, runnable, duration, delay, period, cancel, true);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, long delay, long period) {
        return repeat(plugin, runnable, Duration.TICKS, delay, period);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, Duration duration, long delay, long period) {
        return repeat(plugin, runnable, duration, delay, period, null);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, long delay, long period) {
        return repeatAsync(plugin, runnable, Duration.TICKS, delay, period);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, Duration duration, long delay, long period) {
        return repeatAsync(plugin, runnable, duration, delay, period, null);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, long period) {
        return repeat(plugin, runnable, Duration.TICKS, period);
    }

    public PistonTask repeat(Plugin plugin, Runnable runnable, Duration duration, long period) {
        return repeat(plugin, runnable, duration, 0, period);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, long period) {
        return repeatAsync(plugin, runnable, Duration.TICKS, period);
    }

    public PistonTask repeatAsync(Plugin plugin, Runnable runnable, Duration duration, long period) {
        return repeatAsync(plugin, runnable, duration, 0, period);
    }

    public void cancel(PistonTask task) {
        task.cancel();
        tasks.remove(task.getId());
    }

    public void cancel(int id) {
        PistonTask task = tasks.get(id);
        if(task == null) {
            return;
        }

        cancel(task);
    }

    public void validate(Plugin plugin, Runnable runnable) {
        Preconditions.checkNotNull(plugin, "Task owners cannot be null");
        Preconditions.checkNotNull(runnable, "Tasks cannot be null");
    }

    public void tick() {
        long tick = server.getTick();
        List<PistonTask> tasks = tickTasks.get(tick);
        for(PistonTask task : tasks) {
            if(task.isCancelled()) {
                task.run();
                if(task.getPeriod() > 0) {
                    create(task.getOwner(), task.getTask(), task.getPeriod(), task.getPeriod(), !task.isSync());
                }
            }
        }

        tickTasks.remove(tick);
    }
    
}
