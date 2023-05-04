package gatech.team4.campusdiscovery.Map;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import gatech.team4.campusdiscovery.R;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public MapInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_map_info_window, null);

        TextView building = view.findViewById(R.id.buildingTextView);

        building.setText(marker.getTitle());

        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

}
