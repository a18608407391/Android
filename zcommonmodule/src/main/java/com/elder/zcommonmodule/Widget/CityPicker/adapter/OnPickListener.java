package com.elder.zcommonmodule.Widget.CityPicker.adapter;


import com.elder.zcommonmodule.Widget.CityPicker.model.City;

public interface OnPickListener {
    void onPick(int position, City data);
    void onLocate();
    void onCancel();
}
