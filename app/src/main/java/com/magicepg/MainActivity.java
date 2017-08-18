package com.magicepg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Alexey
 * @since 8/17/17
 */
public final class MainActivity extends AppCompatActivity {

    @OnClick(R.id.open_wheel)
    void openWheel() {
        showWheelPage();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
    }

    private void showWheelPage() {
        final DialogFragment fragmentChannelsWheel = new FragmentChannelsWheel();
        fragmentChannelsWheel.show(getSupportFragmentManager(), FragmentChannelsWheel.TAG);
    }
}
