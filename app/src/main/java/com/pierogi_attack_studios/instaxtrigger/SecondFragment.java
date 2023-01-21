package com.pierogi_attack_studios.instaxtrigger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



public class SecondFragment extends Fragment {

    Bluetooth bluetooth;
    MainActivity activity;
    TextView textView;

    public SecondFragment(Bluetooth bluetooth)
    {
        this.bluetooth = bluetooth;

        bluetooth.deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        bluetooth.deviceInterface.sendMessage("Hello world!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (MainActivity) getActivity();

            if(bluetooth != null)
            {
                bluetooth.activity = activity;
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(bluetooth != null)
        {
            if(bluetooth.activity == null)
                bluetooth.activity = activity;
        }

        view.findViewById(R.id.trigger).setOnClickListener(view1 -> {
            bluetooth.deviceInterface.sendMessage("0");

        });

        view.findViewById(R.id.trigger3s).setOnClickListener(view1 -> {
            bluetooth.deviceInterface.sendMessage("3");

        });

        view.findViewById(R.id.trigger5s).setOnClickListener(view1 -> {
            bluetooth.deviceInterface.sendMessage("5");

        });
        textView = view.findViewById(R.id.battery);
    }

    private void onMessageSent(String message) {
    }

    private void onMessageReceived(String message) {
        Log.d("msg", message);
    }

    private void onError(Throwable error) {
        // Handle the error
    }

    @Override
    public void onDestroyView()
    {
        bluetooth.close();
        super.onDestroyView();
    }



}