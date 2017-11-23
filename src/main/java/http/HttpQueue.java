package http;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;

@Log4j
public class HttpQueue implements Runnable {
    private static Queue<HttpEvent> httpQueue = new PriorityQueue<>();

    @Synchronized
    public void addToQueue(HttpEvent event) {
        httpQueue.add(event);
    }

    @Override
    public void run() {
        while (true) {
            if (httpQueue.peek() != null) {
                Executors.newSingleThreadExecutor().submit(new UploadThread(httpQueue.poll()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error(e);

            }
        }
    }
}
