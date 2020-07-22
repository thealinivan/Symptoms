package com.example.symptoms;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;


public class APIMedical {

    //host
    final static String HOST = "priaid-symptom-checker-v1.p.rapidapi.com";

    //key
    final static String KEY = "API key here";

    //ID's
    public static String[] locationID = {"6", "7", "10", "15", "16", "17",}; //to be replaced with db query in the future

    //query URL
    final static String allBodyLocationsURL = "https://priaid-symptom-checker-v1.p.rapidapi.com/body/locations?language=en-gb";
    final static String allBodySubLocationsURL = "https://priaid-symptom-checker-v1.p.rapidapi.com/body/locations/"+locationID+"?language=en-gb";

    //Http request
    public static String getApiResponse(String queryURL){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(queryURL)
                .get()
                .addHeader("x-rapidapi-host", HOST)
                .addHeader("x-rapidapi-key", KEY)
                .build();
        try {
            ResponseBody responseBody = client.newCall(request).execute().body();
            String data = responseBody.string();
            return data;
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get and store all body locations to Firebase
    public static void getAllBodyLocations() {
            String data = getApiResponse(allBodyLocationsURL);
            Gson gson = new Gson();
            List<BodyLocation> list = gson.fromJson(data, new TypeToken<List<BodyLocation>>() {}.getType());
            list.forEach(new Consumer<BodyLocation>() {
                @Override
                public void accept(BodyLocation bLocation) {
                    DatabaseReference dbRef;
                    dbRef = FirebaseDatabase.getInstance().getReference();
                        //Gson gson = new Gson();       //Logs
                        //Log.d("Obj: ", gson.toJson(bLocation));
                    dbRef.child("Body").child("Locations").child(String.valueOf(bLocation.getID())).setValue(bLocation);
                }
            });
    }

    //get and store all body sublocations to Firebase
    public static void getAllBodySubLocations(String[] locations){
                final String[] loc = locations;
                String data = getApiResponse(allBodySubLocationsURL);
                Gson gson = new Gson();
                List<BodySubLocation> list = gson.fromJson(data, new TypeToken<List<BodySubLocation>>() {}.getType());
                list.forEach(new Consumer<BodySubLocation>() {
                    @Override
                    public void accept(BodySubLocation bLocation) {
                        DatabaseReference dbRef;
                        dbRef = FirebaseDatabase.getInstance().getReference();
                        for (int i=0; i < loc.length; i++){
                            dbRef.child("Body").child("Sublocations").child(String.valueOf(loc[i])).child(String.valueOf(bLocation.getID())).setValue(bLocation);
                        }
                    }
                });
    }

    //get and store all symptoms for a given sublocation
    public static void getSymptomsForBodySubLocation(String subLocationID, User user){
        final String ageBasedGender = getAgeBasedGender(user);
        final String subLocation = subLocationID;
        String allSymptomsForBodySublocation = "https://priaid-symptom-checker-v1.p.rapidapi.com/symptoms/"+subLocationID+"/"+ ageBasedGender +"?language=en-gb";
        String data = getApiResponse(allSymptomsForBodySublocation);
        Log.d("response: ", data);
        Gson gson = new Gson();
        List<Symptom> list = gson.fromJson(data, new TypeToken<List<Symptom>>(){}.getType());
        list.forEach(new Consumer<Symptom>() {
            @Override
            public void accept(Symptom symp) {
                    DatabaseReference dbRef;
                    dbRef = FirebaseDatabase.getInstance().getReference();
                    dbRef.child("Body").child("Symptoms").child(String.valueOf(subLocation)).child(String.valueOf(symp.getID())).setValue(symp);
                }
            });
    }

    //get and store all diagnosis based on case symptoms
    public static void getAllDiagnosisForSymptompsCase(String symptomsIDs, User user){
        String allSymptomsForBodySublocation = "https://priaid-symptom-checker-v1.p.rapidapi.com/diagnosis?symptoms="+symptomsIDs+"&gender="+user.getGender()+"&year_of_birth="+user.getYOB()+"&language=en-gb";
        String data = getApiResponse(allSymptomsForBodySublocation);
        Gson gson = new Gson();
        List<Diagnosis> list = gson.fromJson(data, new TypeToken<List<Diagnosis>>() {}.getType());
        list.forEach(new Consumer<Diagnosis>() {
            @Override
            public void accept(Diagnosis symp) {
                DatabaseReference dbRef;
                dbRef = FirebaseDatabase.getInstance().getReference();

                //setup Diagnosis object class and then decide database structure for transfer
            }
        });
    }

    //get and store all health issues info based on diagnosis/health issues id
    public static void getHealthIssueInfoForHealthIssue(String healthIssueID){
        String allSymptomsForBodySublocation = "https://priaid-symptom-checker-v1.p.rapidapi.com/issues/"+healthIssueID+"/info?language=en-gb";
        String data = getApiResponse(allSymptomsForBodySublocation);
        Gson gson = new Gson();
        List<DiagnosisInfo> list = gson.fromJson(data, new TypeToken<List<DiagnosisInfo>>() {}.getType());
        list.forEach(new Consumer<DiagnosisInfo>() {
            @Override
            public void accept(DiagnosisInfo symp) {
                DatabaseReference dbRef;
                dbRef = FirebaseDatabase.getInstance().getReference();

                //setup DiagnosisInfo object class and then decide database structure for transfer
            }
        });
    }



    //gender formatter
    public static String getAgeBasedGender (User user){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - user.getYOB();
        String ageBasedGender = "";
        switch(user.getGender()){
            case "male":
                if (age < 12){
                    ageBasedGender = "boy";
                } else {
                    ageBasedGender = "man";
                }
                break;

            case "female":
                if (age < 12){
                    ageBasedGender = "girl";
                }else{
                    ageBasedGender = "woman";
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + user.getGender());
        }
        return ageBasedGender;
    }

}
