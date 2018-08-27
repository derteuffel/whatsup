package derteuffel.com.whatsup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    public List<PostStudent> _post_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    public PostRecyclerAdapter(List<PostStudent> _post_list){

        this._post_list=_post_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);

        context= parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String title_data = _post_list.get(position).getTitle();
        holder.setTitleText(title_data);

        //load image

       /* String image_url= _post_list.get(position).getImage_url();
        holder.setImage_url(image_url);*/

        String user_id=_post_list.get(position).getUser_id();
        //User data will be retrieve here

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if (task.isSuccessful()){

               String userName= task.getResult().getString("name");
               String userImage= task.getResult().getString("image");

               holder.setUserData(userName,userImage);
           }else {
               //Firebase Exception
           }
            }
        });



//load posted time
        long millisecond = _post_list.get(position).getTimeStamp().getTime();
        String dateString = android.text.format.DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();

        holder.setTime(dateString);

        //load  userName


    }

    @Override
    public int getItemCount() {
        return _post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView title_view;
        private ImageView post_image_view;
       private TextView post_date_view;
       private TextView post_user_name_view;
       private CircleImageView post_user_image_view;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTitleText(String titleText){
            title_view= mView.findViewById(R.id.post_title_show);
            title_view.setText(titleText);
        }

      /*  public void setImage_url(String downloadUri){

            post_image_view =mView.findViewById(R.id.post_image);
            Glide.with(context).load(downloadUri).into(post_image_view);

        }*/

        public void  setTime(String date){

            post_date_view= mView.findViewById(R.id.post_date);
            post_date_view.setText(date);

        }

        public void setUserData(String name, String image){
            post_user_name_view=mView.findViewById(R.id.postHome_user_name);
            post_user_image_view= mView.findViewById(R.id.mainHome_image_view);

            RequestOptions placeHolderOption= new RequestOptions();
            placeHolderOption.placeholder(R.drawable.ic_launcher_back);

            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(post_user_image_view);
            post_user_name_view.setText(name);

        }
    }
}
