package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.Status;
import de.metzgore.beansplan.shared.IScheduleViewModel;
import de.metzgore.beansplan.shared.ScheduleRepository;

public class DailyScheduleViewModel extends ViewModel implements IScheduleViewModel<DailySchedule> {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final MutableLiveData<Resource<DailySchedule>> schedule = new MutableLiveData<>();
    private final MediatorLiveData<Resource<DailySchedule>> scheduleMerger = new MediatorLiveData<>();
    public LiveData<Boolean> isEmpty = Transformations.map(scheduleMerger, schedule -> schedule == null || schedule.data == null || schedule.data.isEmpty());
    public LiveData<Boolean> isLoading = Transformations.map(scheduleMerger, schedule -> schedule.status == Status.LOADING);

    @Inject
    public DailyScheduleViewModel(ScheduleRepository scheduleRepo) {
        LiveData<Resource<DailySchedule>> scheduleFromRepo = Transformations.switchMap(refresh, scheduleRepo::loadScheduleOfToday);
        scheduleMerger.addSource(scheduleFromRepo, scheduleMerger::setValue);
    }

    public DailyScheduleViewModel(Resource<DailySchedule> scheduleResource) {
        scheduleMerger.addSource(schedule, scheduleMerger::setValue);
        this.schedule.setValue(scheduleResource);
    }

    public LiveData<Resource<DailySchedule>> getSchedule() {
        return scheduleMerger;
    }

    @Override
    public void loadScheduleFromNetwork() {
        refresh.setValue(true);
    }

    @Override
    public void loadSchedule() {
        refresh.setValue(isEmpty.getValue() == null || isEmpty.getValue());
    }

    public void setSchedule(Resource<DailySchedule> scheduleResource) {
        schedule.setValue(scheduleResource);
    }
}
