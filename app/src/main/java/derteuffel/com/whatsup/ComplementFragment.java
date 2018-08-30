package derteuffel.com.whatsup;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComplementFragment extends Fragment {


   // private Activity context;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;



    private RecyclerView _post_list_view;
    private List<PostStudent> _post_list;

    private PostRecyclerAdapter postRecyclerAdapter;

    private FloatingActionButton _add_post_fbtn;
    private  DocumentSnapshot lastVisible;
    


    public ComplementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_complement, container, false);

       // context= getActivity();


        _post_list_view=(RecyclerView)view.findViewById(R.id.post_list_view);
        _post_list= new ArrayList<>();
        _add_post_fbtn=(FloatingActionButton)view.findViewById(R.id.add_post_fbtn);

        _add_post_fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(getActivity(),NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });


        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity());
        _post_list_view.setLayoutManager(layoutManager);
        //_post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        _post_list_view.setAdapter(postRecyclerAdapter);


        postRecyclerAdapter= new PostRecyclerAdapter(_post_list);


        mAuth=FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){


        firebaseFirestore=FirebaseFirestore.getInstance();

     /*   _post_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedBottom= !recyclerView.canScrollVertically(1);

                if (reachedBottom){
                        String title= lastVisible.getString("title");
                        Toast.makeText(container.getContext(),"Reached:"+title, Toast.LENGTH_SHORT).show();
                        loadMorePost();
                }
            }
        });*/

            Query firstQuery= firebaseFirestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

               //  lastVisible= documentSnapshots.getDocuments().get(documentSnapshots.size() -1);

                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if (doc.getType()==DocumentChange.Type.ADDED){
                        PostStudent postStudent= doc.getDocument().toObject(PostStudent.class);

                        _post_list.add(postStudent);

                        postRecyclerAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        }

        // Inflate the layout for this fragment
        return view;
    }

   /* public void loadMorePost(){
        Query nextQuery= firebaseFirestore.collection("Posts")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()){


                lastVisible= documentSnapshots.getDocuments().get(documentSnapshots.size() -1);

                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){

                     if (doc.getType()==DocumentChange.Type.ADDED){
                        PostStudent postStudent= doc.getDocument().toObject(PostStudent.class);

                        _post_list.add(postStudent);

                        postRecyclerAdapter.notifyDataSetChanged();
                    }

                }
                }
            }
        });

    }*/




}
