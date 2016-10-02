package com.novext.taxerapp.models;

import com.novext.taxerapp.OkHttpRequest;

import java.util.ArrayList;

/**
 * Created by angel on 9/30/16.
 */

public class Stop {

    static String pathname = "/stops";

    String description;
    double latitude;
    double longitude;
    TaxiDriver taxiDriver;


    public Stop(){
        taxiDriver = new TaxiDriver();
    }

}
