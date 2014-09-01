package org.pistonmc.stickypiston.scheduler;

import org.pistonmc.plugin.Plugin;

public class StickyAsyncTask extends StickyTask {

    public StickyAsyncTask(Plugin owner, Runnable runnable, long delay, long period) {
        super(owner, runnable, delay, period);
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                superStart();
            }
        }).start();
    }

    public void superStart() {
        super.run();
    }

}
