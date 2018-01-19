package arvapp.navigation;

    import android.os.Bundle;
    import android.support.annotation.Nullable;
    import android.support.design.widget.TabLayout;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentPagerAdapter;
    import android.support.v4.view.ViewPager;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

public class TabFragment extends Fragment {

        public static TabLayout tabLayout;
        public static ViewPager viewPager;
        public static int int_items = 3 ;
        View aux;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View x =  inflater.inflate(R.layout.tab_layout,null);
            aux = x;
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

            viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                }
            });

            return x;
        }

        class MyAdapter extends FragmentPagerAdapter{

            public MyAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position)
            {
                switch (position){
                    case 0 : return new ModeFragment();
                    case 1 : return new MapFragment();
                    case 2 : return new StatFragment();
                }
                return null;
            }


            @Override
            public int getItemPosition(Object ob){
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return int_items;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position){
                    case 0 :
                        return "Mode";
                    case 1 :
                        return "Map";
                    case 2 :
                        return "States";
                }
                return null;
            }
        }

    }


