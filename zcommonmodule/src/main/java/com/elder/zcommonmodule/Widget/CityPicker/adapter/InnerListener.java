package com.elder.zcommonmodule.Widget.CityPicker.adapter;


import com.elder.zcommonmodule.Widget.CityPicker.model.City;

public interface InnerListener {
    void dismiss(int position, City data);
    void locate();
}
