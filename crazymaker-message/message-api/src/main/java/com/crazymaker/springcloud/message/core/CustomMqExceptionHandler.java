package com.crazymaker.springcloud.message.core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.impl.AMQConnection;
import com.rabbitmq.client.impl.ForgivingExceptionHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Default implementation of {@link com.rabbitmq.client.ExceptionHandler}
 * used by {@link AMQConnection}.
 */
public class CustomMqExceptionHandler extends ForgivingExceptionHandler implements ExceptionHandler
{
    @Override
    public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception)
    {
//        log("An unexpected connection driver error occured", exception);
    }


    @Override
    public void handleReturnListenerException(Channel channel, Throwable exception)
    {
        handleChannelKiller(channel, exception, "ReturnListener.handleReturn" );
    }

    @Override
    public void handleConfirmListenerException(Channel channel, Throwable exception)
    {
        handleChannelKiller(channel, exception, "ConfirmListener.handle{N,A}ck" );
    }

    @Override
    public void handleBlockedListenerException(Connection connection, Throwable exception)
    {
        handleConnectionKiller(connection, exception, "BlockedListener" );
    }

    @Override
    public void handleConsumerException(Channel channel, Throwable exception,
                                        Consumer consumer, String consumerTag,
                                        String methodName)
    {
        String logMessage = "Consumer " + consumer
                + " (" + consumerTag + ")"
                + " method " + methodName
                + " for channel " + channel;
        String closeMessage = "Consumer"
                + " (" + consumerTag + ")"
                + " method " + methodName
                + " for channel " + channel;
        handleChannelKiller(channel, exception, logMessage, closeMessage);
    }

    @Override
    protected void handleChannelKiller(Channel channel, Throwable exception, String what)
    {
        handleChannelKiller(channel, exception, what, what);
    }

    protected void handleChannelKiller(Channel channel, Throwable exception, String logMessage, String closeMessage)
    {
        log(logMessage + " threw an exception for channel " + channel, exception);
        try
        {
            channel.close(AMQP.REPLY_SUCCESS, "Closed due to exception from " + closeMessage);
        } catch (AlreadyClosedException ace)
        {
            // noop
        } catch (TimeoutException ace)
        {
            // noop
        } catch (IOException ioe)
        {
            log("Failure during close of channel " + channel + " after " + exception, ioe);
            channel.getConnection().abort(AMQP.INTERNAL_ERROR, "Internal error closing channel for " + closeMessage);
        }
    }

    protected void handleConnectionKiller(Connection connection, Throwable exception, String what)
    {
        log(what + " threw an exception for connection " + connection, exception);
        try
        {
            connection.close(AMQP.REPLY_SUCCESS, "Closed due to exception from " + what);
        } catch (AlreadyClosedException ace)
        {
            // noop
        } catch (IOException ioe)
        {
            log("Failure during close of connection " + connection + " after " + exception, ioe);
            connection.abort(AMQP.INTERNAL_ERROR, "Internal error closing connection for " + what);
        }
    }
}

