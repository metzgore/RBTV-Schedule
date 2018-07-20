package de.metzgore.beansplan.reminders

import android.content.Context
import android.text.format.DateUtils
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.shared.OnReminderButtonClickListener

class ReminderItemViewModel(private val showWithReminder: ShowWithReminder, private val listener:
OnReminderButtonClickListener?) {

    val title: String = showWithReminder.show.title

    fun getShowTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, showWithReminder.show.timeStart.time, DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE) +
                " - " +
                DateUtils.formatDateTime(context, showWithReminder.show.timeEnd.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun getReminderDateTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, showWithReminder.reminder!![0].timestamp.time, DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE)
    }

    fun upsertReminder() {
        listener?.onUpsertReminder(showWithReminder)
    }

    fun deleteReminder() {
        listener?.deleteReminder(showWithReminder)
    }
}