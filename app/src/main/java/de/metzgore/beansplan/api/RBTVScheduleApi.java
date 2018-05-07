package de.metzgore.beansplan.api;

import android.arch.lifecycle.LiveData;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.WeeklySchedule;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RBTVScheduleApi {
    @GET("/api/1.0/schedule/schedule.json")
    LiveData<ApiResponse<WeeklySchedule>> scheduleOfCurrentWeek();

    @GET("/api/1.0/schedule/{year}/{month}/{day}.json")
    LiveData<ApiResponse<DailySchedule>> scheduleOfDay(@Path("year") int year, @Path("month") String month, @Path("day") String day);
}
