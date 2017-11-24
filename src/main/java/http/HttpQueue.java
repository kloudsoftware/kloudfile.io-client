package http;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Log4j
public class HttpQueue {
    private static Queue<HttpEvent> httpQueue = new PriorityQueue<>();
    private Future<?> task;
    private HttpQueueTask httpQueueTask;

    @Synchronized
    public void addToQueue(HttpEvent event) {
        log.info("Added event to queue");
        httpQueue.add(event);
    }

    public void start() {
        if (null != task) {
            return;
        }
        httpQueueTask = new HttpQueueTask();
        task = Executors.newSingleThreadExecutor().submit(httpQueueTask);
        log.info("started task");
    }

    @Synchronized
    public void stop(boolean mayInterruptIfRunning) {
        if (null == task) {
            return;
        }
        httpQueueTask.stop();
        task.cancel(mayInterruptIfRunning);
    }

    @Synchronized
    public void waitAndStop() {
        if (null == task) {
            return;
        }

        try {
            task.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e);
        }
    }

    @Log4j
    private static class HttpQueueTask implements Runnable {
        private boolean running = true;

        void stop() {
            running = false;
        }

        @Override
        public void run() {
            Future<?> lastTask = null;

            while (running) {
                if (httpQueue.peek() != null) {
                    lastTask = Executors.newSingleThreadExecutor().submit(new UploadThread(httpQueue.poll()));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }

            if (null == lastTask) {
                return;
            }

            try {
                if (!lastTask.isDone()) {
                    lastTask.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error(e);
            }
        }
    }
}
