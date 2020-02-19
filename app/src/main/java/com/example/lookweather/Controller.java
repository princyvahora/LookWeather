package com.example.lookweather;


import android.app.Activity;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class Controller {

    public void navigateToFragment(int fragID, Activity curAct, Bundle b)
    {
        NavController navController = Navigation.findNavController(curAct,R.id.host_fragment);
        navController.navigate(fragID,b);
    }
}