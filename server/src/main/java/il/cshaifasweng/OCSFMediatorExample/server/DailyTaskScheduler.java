package il.cshaifasweng.OCSFMediatorExample.server;

import java.time.*;
import java.util.concurrent.*;

public class DailyTaskScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void scheduleDailyAtTime(int hour, int minute, Runnable task) {
        Runnable dailyTask = () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        long initialDelay = computeInitialDelayToTime(hour, minute);
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(dailyTask, initialDelay, period, TimeUnit.SECONDS);
    }

    private static long computeInitialDelayToTime(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        return duration.getSeconds();
    }
}
