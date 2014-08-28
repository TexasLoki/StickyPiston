package org.pistonmc.stickypiston.exception;

import org.pistonmc.Piston;
import org.pistonmc.event.error.ExceptionCaughtEvent;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof Exception) {
            Piston.getEventManager().call(new ExceptionCaughtEvent((Exception) e));
        }
    }

}
