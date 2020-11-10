package com.humanid.auth.data.source.local.preference.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.humanid.auth.data.model.User;
import com.humanid.util.Preconditions;

public class UserPreference {

    private final static String TAG = UserPreference.class.getSimpleName();

    private final static String SHARED_PREF_NAME = "com.humanid.auth";
    private final static String USER_KEY = "USER";

    private static volatile UserPreference INSTANCE;

    private final Context applicationContext;
    private final SharedPreferences sharedPreferences;

    private UserPreference(@NonNull Context applicationContext,
                           @NonNull SharedPreferences sharedPreferences) {
        this.applicationContext = applicationContext;
        this.sharedPreferences = sharedPreferences;
    }

    public static UserPreference getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (UserPreference.class) {
                if (INSTANCE == null) {
                    Context applicationContext = context.getApplicationContext();
                    SharedPreferences sharedPreferences = applicationContext
                            .getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    INSTANCE = new UserPreference(applicationContext, sharedPreferences);
                }
            }
        }

        return INSTANCE;
    }

    @Nullable
    public User load() {
        User user = null;

        if (hasCachedUser()) {
            user = getCachedUser();
        }

        return user;
    }

    @NonNull
    public LiveData<User> loadLiveData() {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        liveData.postValue(load());
        return liveData;
    }

    public void save(@NonNull User user) {
        Preconditions.checkNotNull(user, "User cannot be null.");

        sharedPreferences.edit().putString(USER_KEY, new Gson().toJson(user)).apply();
    }

    public void clear() {
        sharedPreferences.edit().remove(USER_KEY).apply();
    }

    private boolean hasCachedUser() {
        return sharedPreferences.contains(USER_KEY);
    }

    @Nullable
    private User getCachedUser() {
        String jsonString = sharedPreferences.getString(USER_KEY, null);

        User user = null;

        if (!TextUtils.isEmpty(jsonString)) {
            user = new Gson().fromJson(jsonString, User.class);
        }

        return user;
    }
}
