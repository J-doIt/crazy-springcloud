package com.crazymaker.springcloud.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class ShutdownHookThread extends Thread
{
    private volatile boolean hasShutdown = false;
    private static AtomicInteger shutdownTimes = new AtomicInteger(0);
    private final Callable callback;

    /**
     * Create the standard hook thread, with a call back, by using {@link Callable} interface.
     *
     * @param name
     * @param callback The call back function.
     */
    public ShutdownHookThread(String name, Callable callback)
    {
        super("JVM退出钩子(" + name + ")");

        this.callback = callback;
    }

    /**
     * Thread run method.
     * Invoke when the jvm shutdown.
     */
    @Override
    public void run()
    {
        synchronized (this)
        {
            System.out.println(getName() + " starting.... ");
            if (!this.hasShutdown)
            {
                this.hasShutdown = true;
                long beginTime = System.currentTimeMillis();
                try
                {
                    this.callback.call();
                } catch (Exception e)
                {
                    System.out.println(getName() + " error: " + e.getMessage());
                }
                long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                System.out.println(getName() + "  耗时(ms): " + consumingTimeTotal);
            }
        }
    }
}