package com.rodico.simplebarcodescanner;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.rodico.simplebarcodescanner.databinding.ActivityMainBinding;

import gk.android.investigator.Investigator;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Investigator.tag = "RoDiCo";
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setModel(new ViewModel(this));
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }
}
