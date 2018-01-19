package arvapp.navigation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ModeFragment extends Fragment {

    int fragmentToDisplay = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootview = null;
        switch (fragmentToDisplay){
            case 1:
                rootview = inflater.inflate(R.layout.mode_rx_layout, container, false);
                Button change = (Button) rootview.findViewById(R.id.rx_chmode_butt);
                ImageButton imageDone = (ImageButton) rootview.findViewById(R.id.rescuedVictim);
                imageDone.setImageResource(R.drawable.ic_done);
                imageDone.setColorFilter(Color.parseColor("#ffffff"));

                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentToDisplay = 2;
                        ViewPager s = (ViewPager) container.findViewById(R.id.viewpager);
                        PagerAdapter p = s.getAdapter();
                        p.notifyDataSetChanged();
                    }
                });
                break;

            case 2:
                rootview = inflater.inflate(R.layout.mode_tx_layout,null);
                change = (Button) rootview.findViewById(R.id.tx_chmode_butt);
                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentToDisplay = 1;
                        ViewPager s = (ViewPager) container.findViewById(R.id.viewpager);
                        PagerAdapter p = s.getAdapter();
                        p.notifyDataSetChanged();
                    }
                });
                break;
        }

        return rootview;
    }
}