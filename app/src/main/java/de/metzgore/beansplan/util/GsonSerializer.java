package de.metzgore.beansplan.util;

import com.google.gson.Gson;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;

import de.metzgore.beansplan.data.Show;
import io.gsonfire.GsonFireBuilder;

public class GsonSerializer<T> implements CacheSerializer<T> {

    private final Gson mGson;
    private final Class<T> mClazz;

    public GsonSerializer(Class<T> clazz) {
        this.mClazz = clazz;

        this.mGson = new GsonFireBuilder()
                .enableHooks(Show.class)
                .createGsonBuilder()
                .setDateFormat("EEE MMM dd HH:mm:ss z yyyy")
                .create();
    }

    @Override
    public T fromString(String data) {
        return mGson.fromJson(data, mClazz);
    }

    @Override
    public String toString(T object) {
        return mGson.toJson(object, mClazz);
    }
}
