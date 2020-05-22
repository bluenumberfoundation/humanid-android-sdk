package com.humanid.auth.data.repository;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.humanid.auth.data.model.User;
import com.humanid.auth.data.source.local.preference.user.UserPreference;
import com.humanid.auth.data.source.remote.api.APIResponse;
import com.humanid.auth.data.source.remote.api.user.UserAPI;
import com.humanid.auth.data.source.remote.api.user.login.check.CheckLoginResponse;
import com.humanid.auth.data.source.remote.api.user.otp.OTPRequest;
import com.humanid.auth.data.source.remote.api.user.otp.OTPResponse;
import com.humanid.auth.data.source.remote.api.user.register.RegisterRequest;
import com.humanid.auth.data.source.remote.api.user.register.RegisterResponse;
import com.humanid.auth.data.source.remote.api.user.revoke.RevokeAccessRequest;
import com.humanid.auth.data.source.remote.api.user.revoke.RevokeAccessResponse;
import com.humanid.auth.util.livedata.AbsentLiveData;
import com.humanid.auth.util.livedata.NetworkBoundResource;
import com.humanid.auth.util.livedata.vo.Resource;
import com.humanid.util.Preconditions;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserRepository {

    private final static String TAG = UserRepository.class.getSimpleName();

    private static volatile UserRepository INSTANCE;

    private final Context applicationContext;
    private User currentUser;
    private final UserPreference userPreference;
    private final UserAPI userAPI;

    private UserRepository(@NonNull Context applicationContext,
                           @NonNull UserPreference userPreference,
                           @NonNull UserAPI userAPI) {
        this.applicationContext = applicationContext;
        this.userPreference = userPreference;
        this.userAPI = userAPI;
    }

    @NonNull
    public static UserRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    Context applicationContext = context.getApplicationContext();
                    UserPreference userPreference = UserPreference.getInstance(applicationContext);
                    UserAPI userAPI = UserAPI.getInstance(applicationContext);

                    INSTANCE = new UserRepository(applicationContext, userPreference, userAPI);
                }
            }
        }

        return INSTANCE;
    }

    @Nullable
    public User getCurrentUser() {
        loadCurrentUser();
        return currentUser;
    }

    public void clearUserPreference(){
        userPreference.clear();
    }

    private void loadCurrentUser() {
        User user = userPreference.load();

        if (user == null) return;

        setCurrentUser(user, false);
    }

    private void setCurrentUser(@Nullable User currentUser) {
        setCurrentUser(currentUser, true);
    }

    private void setCurrentUser(@Nullable User currentUser, boolean saveToCache) {
        this.currentUser = currentUser;

        if (!saveToCache) return;

        if (currentUser != null) {
            userPreference.save(currentUser);
        } else {
            userPreference.clear();
        }
    }

    @NonNull
    public LiveData<Resource<String>> requestOTP(
            @NonNull String countryCode, @NonNull String phone, @NonNull String applicationID,
            @NonNull String applicationSecret) {
        Preconditions.checkArgument(!TextUtils.isEmpty(countryCode), "countryCode");
        Preconditions.checkArgument(!TextUtils.isEmpty(phone), "phone");

        return new NetworkBoundResource<String, OTPResponse>() {

            private String result;

            @NonNull
            @Override
            protected LiveData<String> loadFromLocal() {
                return new LiveData<String>() {
                    private AtomicBoolean started = new AtomicBoolean(false);
                    @Override
                    protected void onActive() {
                        super.onActive();

                        if (started.compareAndSet(false, true)){
                            postValue("");
                        }
                    }
                };
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<APIResponse<OTPResponse>> createCall() {
                OTPRequest request = new OTPRequest(countryCode, phone, applicationID,
                        applicationSecret);
                return userAPI.requestOTP(request);
            }

            @Override
            protected void saveCallResult(@NonNull OTPResponse item) {
                result = "Request OTP Succeed";
            }
        }.asLiveData();
    }

    @NonNull
    public LiveData<Resource<User>> register(
            @NonNull String countryCode, @NonNull String phone, @NonNull String verificationCode,
            @NonNull String deviceID, @NonNull String notificationID,
            @NonNull String applicationID, @NonNull String applicationSecret) {
        Preconditions.checkArgument(!TextUtils.isEmpty(countryCode), "countryCode");
        Preconditions.checkArgument(!TextUtils.isEmpty(phone), "phone");
        Preconditions.checkArgument(!TextUtils.isEmpty(verificationCode), "verificationCode");

        return new NetworkBoundResource<User, RegisterResponse>() {
            @NonNull
            @Override
            protected LiveData<User> loadFromLocal() {
                return userPreference.loadLiveData();
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<APIResponse<RegisterResponse>> createCall() {
                RegisterRequest request = new RegisterRequest(countryCode, phone, verificationCode,
                        deviceID, notificationID, applicationID, applicationSecret);

                return userAPI.register(request);
            }

            @Override
            protected void saveCallResult(@NonNull final RegisterResponse item) {
                User user = new User(item.getRegisterItem().getUserHash(), item.getRegisterItem().getExchangeToken());
                setCurrentUser(user);
            }
        }.asLiveData();
    }

    @NonNull
    public LiveData<Resource<String>> loginCheck(
            @NonNull String userHash, @NonNull String applicationID,
            @NonNull String applicationSecret) {
        Preconditions.checkArgument(!TextUtils.isEmpty(userHash), "userHash");

        return new NetworkBoundResource<String, CheckLoginResponse>() {

            private String result;

            @NonNull
            @Override
            protected LiveData<String> loadFromLocal() {
                if (result == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<String>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            postValue(result);
                        }
                    };
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<APIResponse<CheckLoginResponse>> createCall() {
                return userAPI.checkLogin(userHash, applicationID, applicationSecret);
            }

            @Override
            protected void saveCallResult(@NonNull CheckLoginResponse item) {
                result = item.getMessage();
            }
        }.asLiveData();
    }

    public void logout() {
        setCurrentUser(null);
    }

    @NonNull
    public LiveData<Resource<String>> revokeAccess(
            @NonNull String applicationID,
            @NonNull String applicationSecret) {

        Preconditions.checkArgument(!TextUtils.isEmpty(userPreference.load().getUserHash()), "userHash");

        return new NetworkBoundResource<String, RevokeAccessResponse>() {

            private String result;

            @NonNull
            @Override
            protected LiveData<String> loadFromLocal() {
                return new LiveData<String>() {
                    private AtomicBoolean started = new AtomicBoolean(false);
                    @Override
                    protected void onActive() {
                        super.onActive();

                        if (started.compareAndSet(false, true)){
                            postValue("");
                        }
                    }
                };
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<APIResponse<RevokeAccessResponse>> createCall() {
                RevokeAccessRequest request = new RevokeAccessRequest(applicationID,
                        applicationSecret, userPreference.load().getUserHash());
                return userAPI.revokeAccess(request);
            }

            @Override
            protected void saveCallResult(@NonNull RevokeAccessResponse item) {
                result = "Revoke Access Succeed";
            }
        }.asLiveData();
    }
}
