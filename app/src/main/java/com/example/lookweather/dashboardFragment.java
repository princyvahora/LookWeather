package com.example.lookweather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class dashboardFragment extends Fragment {


    WebView webView;

    ArrayList<ConsolidatedWeather> consolidatedWeather;

    ArrayList<Source> sources;


    TextView txt_name, weather_name, temp_min, the_temp, temp_max, humidity, predictability, more_info,city_name;
    ImageView weather_img;


    ConstraintLayout constraintLayout;

    View mainView;
    Bundle bundle;


    RecyclerView recyclerView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return mainView;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weather_name = mainView.findViewById(R.id.txt_dashname);
        weather_img = mainView.findViewById(R.id.img_weather);
        city_name = mainView.findViewById(R.id.weather_name);
        temp_min = mainView.findViewById(R.id.temp_min);
        the_temp = mainView.findViewById(R.id.the_temp);
        temp_max = mainView.findViewById(R.id.temp_max);
        humidity = mainView.findViewById(R.id.humidity);
        predictability = mainView.findViewById(R.id.predictability);
        recyclerView = mainView.findViewById(R.id.recyclerview);
        more_info = mainView.findViewById(R.id.more_info);

        more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                final WebViewFragment webViewFragment = new WebViewFragment();

                 fragmentTransaction.add(R.id.host_fragment, webViewFragment, "web");

                webViewFragment.setArguments(bundle);

                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

                System.out.println("From More info Method " + getFragmentManager().getBackStackEntryCount());


            }

        });


        temp_min = mainView.findViewById(R.id.temp_min);
        temp_max = mainView.findViewById(R.id.temp_max);
        humidity = mainView.findViewById(R.id.humidity);
        predictability = mainView.findViewById(R.id.predictability);


        constraintLayout = mainView.findViewById(R.id.constrain);


        getWeather();
    }


    public void getWeather() {
        Getdataservice service = RetroFitInstance.getRetrofitInstance().create(Getdataservice.class);

        Call<Weather> call = service.getWeather(getArguments().getInt("geoid"));

        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {


                Weather weather = response.body();

                sources = new ArrayList<>(weather.getSources());
                Parent parent = weather.getParent();

                consolidatedWeather = new ArrayList<>(weather.getConsolidatedWeather());

                bundle = new Bundle();
                //bundle.putString("bbc_url","https://www.bbc.com/weather/6077243");
                bundle.putString("bbc_url", sources.get(0).getUrl() + getArguments().getInt("bbcid"));
                bundle.putString("city", weather.getTitle());


                //weather_name.setText(sources.get(0).getUrl());
                city_name.setText(weather.getTitle() + ",\n" + parent.getTitle());
                the_temp.setText(String.format("%.2f", consolidatedWeather.get(0).getTheTemp()) + "°C");
                temp_min.setText(String.format("%.2f", consolidatedWeather.get(0).getMaxTemp()));
                temp_max.setText(String.format("%.2f", consolidatedWeather.get(0).getMinTemp()));
                humidity.setText(consolidatedWeather.get(0).getHumidity().toString() + "%");
                predictability.setText(consolidatedWeather.get(0).getPredictability().toString() + "%");

                Picasso.get().load(getWeatherImage(consolidatedWeather.get(0).getWeatherStateAbbr())).into(weather_img);


                initForecastWeather(consolidatedWeather);

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(weather.getTitle().toUpperCase());


                String city = weather.getTitle();


            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {

                System.out.println("Failure Called! :" + t.getMessage());
            }
        });
    }

    public String getWeatherImage(String weatherStateAbbr) {
        return "https://www.metaweather.com/static/img/weather/png/" + weatherStateAbbr + ".png";
    }

    public void initForecastWeather(ArrayList<ConsolidatedWeather> consolidatedWeather) {
        RecyclerView recyclerView = mainView.findViewById(R.id.recyclerview);
        consolidatedWeather.remove(0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        Recyadapter adapter = new Recyadapter(consolidatedWeather, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);
    }


}