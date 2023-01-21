package com.pierogi_attack_studios.instaxtrigger;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;


public class Bluetooth {

    BluetoothManager bluetoothManager;
    MainActivity activity;
    SimpleBluetoothDeviceInterface deviceInterface;
    Fragment fragment;

    public Bluetooth(MainActivity activity, Fragment fragment)
    {
        this.fragment = fragment;
        this.activity = activity;
        bluetoothManager = BluetoothManager.getInstance();
        if (bluetoothManager == null) {
            Toast.makeText(activity, "Bluetooth is not available!", Toast.LENGTH_LONG).show();
            activity.finish();
        }


    }

    public void close()
    {
        bluetoothManager.closeDevice(deviceInterface);
        bluetoothManager.close();
    }




}
