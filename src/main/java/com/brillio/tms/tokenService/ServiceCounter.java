package com.brillio.tms.tokenService;

import com.brillio.tms.tokenGeneration.Token;
import com.brillio.tms.tokenGeneration.TokenCategory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceCounter implements IServiceCounter {

    private final BlockingQueue<Token> tokensQueue;
    private final static int MAX_REQUESTS = 50;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final TokenCategory category;
    private ExecutorService executorService;

    private final int counterNumber;

    public ServiceCounter(int counterNumber, TokenCategory category) {
        this.counterNumber = counterNumber;
        tokensQueue = new LinkedBlockingQueue<>(MAX_REQUESTS);
        this.category = category;
        this.startCounter();
    }


    public void startCounter() {
        if(!isStarted.getAndSet(true)) {
            executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("ServiceCounter_" + ServiceCounter.this.counterNumber);
                    return t;
                }
            });

            executorService.submit(() -> {
               while (isStarted.get()) {
                   try {
                       Token token = tokensQueue.take();
                       if(Token.EMPTY_TOKEN.equals(token)) {
                           tokensQueue.clear();
                           break;
                       }
                       System.out.println("Serving applicant with token no. " +
                               token.getTokenNumber() + " at counter no. " + counterNumber);
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
            });
        }
    }

    @Override
    public void serveToken(Token token) {
        try {
            tokensQueue.put(token);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopCounter() {
        if(isStarted.getAndSet(false)) {
            try {
                tokensQueue.put(Token.EMPTY_TOKEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceCounter that = (ServiceCounter) o;

        return counterNumber == that.counterNumber;
    }

    @Override
    public int hashCode() {
        return counterNumber;
    }
}
