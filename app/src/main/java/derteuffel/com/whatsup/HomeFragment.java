package derteuffel.com.whatsup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String Url="";
    private List<PostAdmin> admins= new ArrayList<PostAdmin>();
    private ListView listView;
    private PostAdminAdapter adapter;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        listView= (ListView) view.findViewById(R.id.postHome_list_view);
        adapter= new PostAdminAdapter(getActivity(),admins);
        listView.setAdapter(adapter);

        return view;


    }

}
