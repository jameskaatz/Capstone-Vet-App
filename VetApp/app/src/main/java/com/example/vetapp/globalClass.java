package com.example.vetapp;

import android.app.Application;

public class globalClass extends Application {

    private Boolean openCase;

    public Boolean getOpenCase() {
        return openCase;
    }

    public void setOpenCase(Boolean openCase) {
        this.openCase = openCase;
    }
}
