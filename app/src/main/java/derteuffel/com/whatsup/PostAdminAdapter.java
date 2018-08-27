package derteuffel.com.whatsup;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostAdminAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<PostAdmin> postAdmins;

    private ImageLoader imageLoader=AppController.getmInstance().getImageLoader();

public PostAdminAdapter(Activity activity, List<PostAdmin> postAdmins){
    this.activity=activity;
    this.postAdmins=postAdmins;
}

    @Override
    public int getCount() {
        return postAdmins.size();
    }

    @Override
    public Object getItem(int i) {
        return postAdmins.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

    if (inflater==null){
        inflater= (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    if (view==null){
        view=inflater.inflate(R.layout.posthome_list_item, null);
    }

    if (imageLoader==null){
        imageLoader=AppController.getmInstance().getImageLoader();
        //NetworkImageView imageView=(NetworkImageView)view.findViewById(R.id.mainHome_image_view);
        TextView title= (TextView)view.findViewById(R.id.titleHome_post_view);
       // TextView user_name= (TextView)view.findViewById(R.id.postHome_user_name);
       // TextView date= (TextView)view.findViewById(R.id.dateHome_post_view);

        //getting data for row

        PostAdmin postAdmin= postAdmins.get(i);

        //image
        //imageView.setImageUrl(postAdmin.getImage(), imageLoader);
        //titre
        title.setText(postAdmin.getTitre());
        //user_name
      //  user_name.setText(postAdmin.getUserName());
        //date
        /*long millisecond = postAdmins.get(i).getTimestamp().getTime();
        String dateString = android.text.format.DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();*/



    }
        return view;
    }
}
