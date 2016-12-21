package com.gdgcincinnati.santatracker;

import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final Marker[] markers = new Marker[1];
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference currentLocationReference = database.getReference("current_location");
        currentLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Remove our our old current position marker
                if (markers[0] != null) {
                    markers[0].remove();
                    markers[0] = null;
                }

                Double lat = dataSnapshot.child("lat").getValue(Double.class);
                Double lng = dataSnapshot.child("lng").getValue(Double.class);

                LatLng currentLocation = new LatLng(lat, lng);
                // Keep a reference to the current location marker
                markers[0] = mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Santa!")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.santa)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final SoundPool soundPool = new SoundPool.Builder().build();
        final int soundId = soundPool.load(this, R.raw.hohoho, 1);

        DatabaseReference hohohoReference = database.getReference("ho_ho_hoing");
        hohohoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hohohoing = dataSnapshot.getValue(Boolean.class);
                if (hohohoing) {
                    soundPool.play(soundId, 1, 1, 10, 0, 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
