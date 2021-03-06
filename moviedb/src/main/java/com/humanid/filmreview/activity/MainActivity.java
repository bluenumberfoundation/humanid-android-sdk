package com.humanid.filmreview.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.humanid.filmreview.R;
import com.humanid.filmreview.adapter.ViewPagerAdapter;
import com.humanid.filmreview.data.login.PostLoginRequest;
import com.humanid.filmreview.data.logout.PutLogoutRequest.OnLogoutCallback;
import com.humanid.filmreview.domain.user.UserInteractor;
import com.humanid.filmreview.domain.user.UserUsecase;
import com.humanid.humanidui.presentation.LoginCallback;
import com.humanid.humanidui.presentation.LoginManager;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;

    @BindView(R.id.vpMain)
    ViewPager vpMain;

    @BindView(R.id.tabMain)
    TabLayout tabMain;

    @BindView(R.id.imgProfile)
    ImageView imgProfile;

    private UserUsecase userUsecase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        vpMain.setAdapter(viewPagerAdapter);
        tabMain.setupWithViewPager(vpMain);

        progressDialog = new ProgressDialog(this);

        userUsecase = new UserInteractor(this);

        imgProfile.setOnClickListener(view -> {
            if (UserInteractor.getInstance(this).isLoggedIn()){
                showLogoutAlerDialog();
            }else{
                LoginManager.registerCallback(this, new LoginCallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, getString(R.string.message_login_canceled), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(@NotNull String exchangeToken) {
                        Log.d("GotExchangeToken", exchangeToken);
                        authenticateUser(exchangeToken);
                    }

                    @Override
                    public void onError(@NotNull String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void authenticateUser(String exchangeToken) {
        userUsecase.login(exchangeToken, new PostLoginRequest.OnLoginCallback() {
            @Override
            public void onLoading() {
                showLoading();
            }

            @Override
            public void onLoginSuccess() {
                hideLoading();
                setUpAvatar(userUsecase.isLoggedIn());
            }

            @Override
            public void onLoginFailed(String message) {
                hideLoading();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showLoading() {
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpAvatar(userUsecase.isLoggedIn());
    }

    private void setUpAvatar(Boolean isLoggeIn) {
        int avatar = 0;
        if (isLoggeIn) {
            avatar = R.drawable.wolverine;
        } else {
            avatar = R.drawable.ic_person_black_24dp;
        }

        Glide.with(this)
                .load(avatar)
                .into(imgProfile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent searchIntent = new Intent(this, SettingActivity.class);
            startActivity(searchIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LoginManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showLogoutAlerDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    logout();
                }).setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();
    }

    private void logout() {
        LoginManager.logout();

        UserInteractor.getInstance(this).logout(new OnLogoutCallback() {
            @Override
            public void onLoading() {
                showLoading();
            }

            @Override
            public void onLogoutSuccess() {
                hideLoading();
                setUpAvatar(false);
                Toast.makeText(MainActivity.this, "Logout Succeed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutFailure(final String message) {
                hideLoading();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnauthorized() {
                setUpAvatar(false);
                Toast.makeText(MainActivity.this, "Your session is expired. Please Login", Toast.LENGTH_SHORT).show();
            }
        });
    }
}