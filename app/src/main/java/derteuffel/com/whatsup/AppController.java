package derteuffel.com.whatsup;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG=AppController.class.getSimpleName();
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }
    public  static synchronized AppController getmInstance  (){
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {

        getRequestQueue();
        if (imageLoader==null){
            imageLoader= new ImageLoader(this.requestQueue, new BitmapCache());
        }
        return this.imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TAG);

        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);

        getRequestQueue().add(request);
    }

    public void cancelPending(Object tag){
        if (requestQueue!=null){
            requestQueue.cancelAll(tag);
        }
    }
}
