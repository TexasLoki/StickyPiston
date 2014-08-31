package org.pistonmc.stickypiston.exception;

import org.pistonmc.Piston;
import org.pistonmc.event.error.ExceptionCaughtEvent;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof Exception) {
            e.printStackTrace();
            Piston.getEventManager().call(new ExceptionCaughtEvent((Exception) e));
        }
    }

}
